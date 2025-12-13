package com.github.razorplay01.inv_view.event;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.command.InvViewCommands;
import com.github.razorplay01.inv_view.mixin.ServerPlayerAccesor;
import com.github.razorplay01.inv_view.util.ITargetPlayerContainer;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.UUID;

@EventBusSubscriber(modid = InvViewNeoforge.MOD_ID)
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
                    InvViewNeoforge.savePlayerData(minecraftServer, container.getTargetPlayer());
                    player.closeContainer();
                    player.displayClientMessage(Component.translatable("inv_view_neoforge.player_connected", joiningPlayer.getName()), false);
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
                    InvViewNeoforge.savePlayerData(minecraftServer, container.getTargetPlayer());
                    player.closeContainer();
                    player.displayClientMessage(Component.translatable("inv_view_neoforge.player_disconnected", leavingPlayer.getName()), false);
                }
            });
        }
    }
}