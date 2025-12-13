package com.github.razorplay01.inv_view.command;

import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.api.InventoryProviderRegistry;
import com.github.razorplay01.inv_view.mixin.EntityAccessor;
import com.github.razorplay01.inv_view.provider.PlayerInventoryProvider;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import com.github.razorplay01.inv_view.util.PermissionHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InvViewCommands {
    public InvViewCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        var viewCommand = Commands.literal("view")
                .requires(source -> source.hasPermission(2));

        for (InventoryProvider provider : InventoryProviderRegistry.getAllProviders()) {
            viewCommand.then(Commands.literal(provider.getId())
                    .requires(source -> PermissionHandler.hasPermission(source, provider.getOpenPermission(), 2))
                    .then(Commands.argument("target", GameProfileArgument.gameProfile())
                            .executes(context -> executeViewCommand(context, provider))));
        }

        dispatcher.register(viewCommand);
    }

    private int executeViewCommand(CommandContext<CommandSourceStack> context, InventoryProvider provider) throws CommandSyntaxException {
        ServerPlayer viewer = context.getSource().getPlayerOrException();
        ServerPlayer target = getRequestedPlayer(context);

        if (viewer == target && provider instanceof PlayerInventoryProvider) {
            context.getSource().sendFailure(Component.translatable("inv_view_neoforge.command.error.invalid_inventory"));
            return 0;
        }

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

    private static ServerPlayer getRequestedPlayer(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        MinecraftServer minecraftServer = context.getSource().getServer();

        NameAndId playerConfigEntry = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
        ServerPlayer requestedPlayer = minecraftServer.getPlayerList().getPlayer(playerConfigEntry.name());

        // If player is not currently online
        if (requestedPlayer == null) {
            requestedPlayer = new ServerPlayer(minecraftServer, minecraftServer.overworld(), new GameProfile(playerConfigEntry.id(), playerConfigEntry.name()),
                    ClientInformation.createDefault());
            Optional<ValueInput> readViewOpt = minecraftServer.getPlayerList()
                    .loadPlayerData(playerConfigEntry).map(playerData -> TagValueInput.create(new ProblemReporter.ScopedCollector(LogUtils.getLogger()), minecraftServer.registryAccess(), playerData));
            readViewOpt.ifPresent(requestedPlayer::load);

            // Avoids player's dimension being reset to the overworld
            if (readViewOpt.isPresent()) {
                ValueInput readView = readViewOpt.get();
                Optional<String> dimension = readView.getString("Dimension");

                if (dimension.isPresent()) {
                    ServerLevel world = minecraftServer.getLevel(
                            ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(dimension.get())));

                    if (world != null) {
                        ((EntityAccessor) requestedPlayer).callSetWorld(world);
                    }
                }
            }
        }

        return requestedPlayer;
    }
}