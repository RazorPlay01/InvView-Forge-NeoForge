package com.github.razorplay01.inv_view.command;

import com.github.razorplay01.inv_view.ModTemplate;
import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.api.InventoryProviderRegistry;
import com.github.razorplay01.inv_view.provider.PlayerInventoryProvider;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import com.github.razorplay01.inv_view.util.PermissionHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InvViewCommands {
	public InvViewCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		//? <=1.21.1{
		/*var viewCommand = Commands.literal("view")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS));
		*///?}
		//? >1.21.1{
		var viewCommand = Commands.literal("view")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
		//?}

		for (InventoryProvider provider : InventoryProviderRegistry.getAllProviders()) {
			viewCommand.then(Commands.literal(provider.getId())
					.requires(source -> PermissionHandler.hasPermission(source, provider.getOpenPermission(), 2))
					.then(Commands.argument("target", GameProfileArgument.gameProfile())
							.executes(context -> executeViewCommand(context, provider))));
		}

		dispatcher.register(viewCommand);
		ModTemplate.LOGGER.info("Command register...");
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

	//? <=1.21.1{
	/*private ServerPlayer getRequestedPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		MinecraftServer server = context.getSource().getServer();
		GameProfile profile = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
		ServerPlayer player = server.getPlayerList().getPlayer(profile.getId());

		if (player == null) {
			// Intenta cargar datos de jugador offline
			try {
				//? 1.20.1{
				/^player = server.getPlayerList().getPlayerForLogin(profile);
				^///?}else{
				player = server.getPlayerList().getPlayerForLogin(profile, net.minecraft.server.level.ClientInformation.createDefault());
				//?}
				if (player == null) {
					throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
							Component.literal("Player " + profile.getName() + " not found."));
				}

				//? 1.21.1{
				/^Optional<net.minecraft.nbt.CompoundTag> compoundOpt = server.getPlayerList().load(player);
				if (compoundOpt.isPresent()) {
					net.minecraft.nbt.CompoundTag compound = compoundOpt.get();
					^///?}else {
				net.minecraft.nbt.CompoundTag compoundOpt = server.getPlayerList().load(player);
				if (compoundOpt != null) {
					net.minecraft.nbt.CompoundTag compound = compoundOpt;
				//?}
					// Leer la dimensión desde el NBT
					String dimensionId = compound.getString("Dimension");
					ResourceKey<net.minecraft.world.level.Level> dimension;
					try {
						//? >=1.20.1{
						dimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, Identifier.parse(dimensionId));
						//?}else {
						//dimension = ResourceKey.create(net.minecraft.core.Registry.DIMENSION_REGISTRY, Identifier.parse(dimensionId));
						//?}
					} catch (Exception e) {
						dimension = net.minecraft.world.level.Level.OVERWORLD;
					}
					ServerLevel level = server.getLevel(dimension);
					if (level != null) {
						//? >=1.20.1{
						player.setServerLevel(level);
						//?}else {
						//player.setLevel(level);
						//?}
					} else {
						//? >= 1.20.1{
						player.setServerLevel(server.overworld());
						//?}else {
						//player.setLevel(server.overworld());
						//?}
					}
				} else {
					//? >= 1.20.1{
					player.setServerLevel(server.overworld());
					//?}else {
					//player.setLevel(server.overworld());
					//?}
				}
			} catch (Exception e) {
				throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
						Component.literal("Failed to load player data for " + profile.getName() + ": " + e.getMessage()));
			}
		}
		return player;
	}
	*///?}
	//? >1.21.1{
	private static ServerPlayer getRequestedPlayer(CommandContext<CommandSourceStack> context)
			throws CommandSyntaxException {
		MinecraftServer minecraftServer = context.getSource().getServer();

		//? >=1.21.8{
		net.minecraft.server.players.NameAndId playerConfigEntry = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
		ServerPlayer requestedPlayer = minecraftServer.getPlayerList().getPlayer(playerConfigEntry.name());
		//?}
		//? <1.21.8{
		/*GameProfile requestedProfile = GameProfileArgument.getGameProfiles(context, "target").iterator().next();
		ServerPlayer requestedPlayer = minecraftServer.getPlayerList().getPlayer(requestedProfile.getId());
		*///?}

		// If player is not currently online
		if (requestedPlayer == null) {
			//? >=1.21.8{
			requestedPlayer = new ServerPlayer(minecraftServer, minecraftServer.overworld(), new GameProfile(playerConfigEntry.id(), playerConfigEntry.name()),
					net.minecraft.server.level.ClientInformation.createDefault());
			Optional<net.minecraft.world.level.storage.ValueInput> readViewOpt = minecraftServer.getPlayerList()
					.loadPlayerData(playerConfigEntry).map(playerData -> net.minecraft.world.level.storage.TagValueInput.create(new net.minecraft.util.ProblemReporter.ScopedCollector(com.mojang.logging.LogUtils.getLogger()), minecraftServer.registryAccess(), playerData));
			//?}
			//? <1.21.8{
			/*requestedPlayer = new ServerPlayer(minecraftServer, minecraftServer.overworld(), requestedProfile,
					net.minecraft.server.level.ClientInformation.createDefault());
			Optional<net.minecraft.world.level.storage.ValueInput> readViewOpt = minecraftServer.getPlayerList()
					.load(requestedPlayer, new net.minecraft.util.ProblemReporter.ScopedCollector(com.mojang.logging.LogUtils.getLogger()));
			*///?}
			readViewOpt.ifPresent(requestedPlayer::load);

			// Avoids player's dimension being reset to the overworld
			if (readViewOpt.isPresent()) {
				net.minecraft.world.level.storage.ValueInput readView = readViewOpt.get();
				Optional<String> dimension = readView.getString("Dimension");

				if (dimension.isPresent()) {
					ServerLevel world = minecraftServer.getLevel(
							ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, Identifier.tryParse(dimension.get())));

					if (world != null) {
						((com.github.razorplay01.inv_view.mixin.EntityAccessor) requestedPlayer).callSetWorld(world);
					}
				}
			}
		}

		return requestedPlayer;
	}
	//?}
}
