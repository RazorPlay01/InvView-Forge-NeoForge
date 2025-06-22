package com.github.razorplay01.inv_view.command;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.api.InventoryProviderRegistry;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import com.github.razorplay01.inv_view.util.PermissionHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InvViewCommands {
    public InvViewCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        var viewCommand = Commands.literal("view")
                .requires(source -> source.hasPermission(2));

        for (InventoryProvider provider : InventoryProviderRegistry.getAllProviders()) {
            viewCommand.then(Commands.literal(provider.getId())
                    .requires(source -> PermissionHandler.hasPermission(source, provider.getPermission(), 2))
                    .then(Commands.argument("target", GameProfileArgument.gameProfile())
                            .executes(context -> executeViewCommand(context, provider))));
        }

        dispatcher.register(viewCommand);
    }

    private int executeViewCommand(CommandContext<CommandSourceStack> context, InventoryProvider provider) throws CommandSyntaxException {
        ServerPlayer viewer = context.getSource().getPlayerOrException();
        ServerPlayer target = getRequestedPlayer(context);

        if (!provider.isAvailable(target)) {
            context.getSource().sendFailure(Component.translatable("inv_view_neoforge.command.error.inventory_not_available"));
            return 0;
        }

        if (InventoryLockManager.isLocked(target.getUUID(), provider.getLockType())) {
            context.getSource().sendFailure(Component.translatable("inv_view_neoforge.command.error.inventory_in_use"));
            return 0;
        }

        openScreen(viewer, target, provider);
        return 1;
    }

    private void openScreen(ServerPlayer viewer, ServerPlayer target, InventoryProvider provider) {
        viewer.openMenu(new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return provider.getDisplayName(target);
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inventory, @NotNull Player player) {
                return provider.createMenu(syncId, viewer, target);
            }
        });
    }

    private ServerPlayer getRequestedPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        GameProfile profile = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
        ServerPlayer player = server.getPlayerList().getPlayer(profile.getId());

        if (player == null) {
            // Intenta cargar datos de jugador offline
            try {
                player = server.getPlayerList().getPlayerForLogin(profile, ClientInformation.createDefault());
                if (player == null) {
                    throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                            Component.literal("Player " + profile.getName() + " not found."));
                }

                Optional<CompoundTag> compoundOpt = server.getPlayerList().load(player);
                if (compoundOpt.isPresent()) {
                    CompoundTag compound = compoundOpt.get();
                    // Leer la dimensión desde el NBT
                    String dimensionId = compound.getString("Dimension");
                    ResourceKey<Level> dimension;
                    try {
                        dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionId));
                    } catch (Exception e) {
                        InvViewNeoforge.LOGGER.warn("Invalid dimension ID {} for player {}, defaulting to overworld", dimensionId, profile.getName());
                        dimension = Level.OVERWORLD;
                    }
                    ServerLevel level = server.getLevel(dimension);
                    if (level != null) {
                        player.setServerLevel(level);
                    } else {
                        // Fallback al mundo principal si la dimensión no existe
                        player.setServerLevel(server.overworld());
                        InvViewNeoforge.LOGGER.warn("Dimension {} not found for player {}, defaulting to overworld", dimensionId, profile.getName());
                    }
                } else {
                    // Si no hay datos NBT, usar el mundo principal
                    player.setServerLevel(server.overworld());
                    InvViewNeoforge.LOGGER.info("No NBT data found for player {}, defaulting to overworld", profile.getName());
                }
            } catch (Exception e) {
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                        Component.literal("Failed to load player data for " + profile.getName() + ": " + e.getMessage()));
            }
        }
        return player;
    }
}