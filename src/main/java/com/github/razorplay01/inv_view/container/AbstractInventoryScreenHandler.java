package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.util.ITargetPlayerContainer;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractInventoryScreenHandler extends AbstractContainerMenu implements ITargetPlayerContainer {
    protected final ServerPlayer targetPlayer;
    protected final UUID targetPlayerUUID;
    protected final InventoryLockManager.InventoryType lockType;
    protected final ServerPlayer viewer;

    protected AbstractInventoryScreenHandler(MenuType<?> type, int syncId, ServerPlayer viewer, ServerPlayer target, InventoryLockManager.InventoryType lockType) {
        super(type, syncId);
        this.targetPlayer = target;
        this.targetPlayerUUID = target.getUUID();
        this.lockType = lockType;
        this.viewer = viewer;
        if (!tryLockInventory(viewer)) {
            viewer.closeContainer();
        }
    }

    protected boolean tryLockInventory(ServerPlayer viewer) {
        if (!InventoryLockManager.tryLock(targetPlayerUUID, lockType)) {
            viewer.displayClientMessage(Component.translatable("inv_view_neoforge.inventory_in_use.error"), false);
            return false;
        }
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        // Sincronizar inventario del jugador objetivo
        if (targetPlayer != null) {
            targetPlayer.getInventory().setChanged();
            if (lockType == InventoryLockManager.InventoryType.ENDER_CHEST) {
                targetPlayer.getEnderChestInventory().setChanged();
            }
            // Guardar datos del jugador objetivo
            InvViewNeoforge.savePlayerData(targetPlayer);
        }
        // Liberar el bloqueo del inventario
        InventoryLockManager.unlock(targetPlayerUUID, lockType);
        super.removed(player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public ServerPlayer getTargetPlayer() {
        return targetPlayer;
    }

    protected abstract void addInventorySlots();
}