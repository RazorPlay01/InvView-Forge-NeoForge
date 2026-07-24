package com.example.modtemplate.event;

import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.command.InvViewCommands;
import com.example.modtemplate.mixin.ServerPlayerAccesor;
import com.example.modtemplate.util.ITargetPlayerContainer;
import com.example.modtemplate.util.InventoryLockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
//?} forge {
/*import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
*///?}


@/*? forge {*//*Mod.*//*?} */EventBusSubscriber(modid = ModTemplate.MOD_ID)
public class ModEvents {
	@SubscribeEvent
	public static void onCommandsRegister(RegisterCommandsEvent event) {
		new InvViewCommands(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer joiningPlayer) {
			UUID joiningPlayerUUID = joiningPlayer.getUUID();
			InventoryLockManager.unlockAll(joiningPlayerUUID);

			MinecraftServer minecraftServer = ((ServerPlayerAccesor) joiningPlayer).server();

			minecraftServer.getPlayerList().getPlayers().forEach(player -> {
				if (player.containerMenu instanceof ITargetPlayerContainer container &&
						container.getTargetPlayer().getUUID().equals(joiningPlayerUUID)) {
					// Guardar datos del jugador objetivo antes de cerrar
					ModTemplate.savePlayerData(minecraftServer, container.getTargetPlayer());
					player.closeContainer();
					//? < 26 {
					/*player.displayClientMessage(Component.translatable("inv_view_neoforge.player_connected", joiningPlayer.getName()), false);
					*///?}
					//? >= 26 {
					player.sendSystemMessage(Component.translatable("inv_view_neoforge.player_connected", joiningPlayer.getName()), false);
					//?}
				}
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer leavingPlayer) {
			UUID leavingPlayerUUID = leavingPlayer.getUUID();
			InventoryLockManager.unlockAll(leavingPlayerUUID);
			MinecraftServer minecraftServer = ((ServerPlayerAccesor) leavingPlayer).server();

			minecraftServer.getPlayerList().getPlayers().forEach(player -> {
				if (player.containerMenu instanceof ITargetPlayerContainer container &&
						container.getTargetPlayer().getUUID().equals(leavingPlayerUUID)) {
					// Guardar datos del jugador objetivo antes de cerrar
					ModTemplate.savePlayerData(minecraftServer, container.getTargetPlayer());
					player.closeContainer();
					//? < 26 {
					/*player.displayClientMessage(Component.translatable("inv_view_neoforge.player_disconnected", leavingPlayer.getName()), false);
					*///?}
					//? >= 26 {
					player.sendSystemMessage(Component.translatable("inv_view_neoforge.player_disconnected", leavingPlayer.getName()), false);
					//?}
				}
			});
		}
	}
}
