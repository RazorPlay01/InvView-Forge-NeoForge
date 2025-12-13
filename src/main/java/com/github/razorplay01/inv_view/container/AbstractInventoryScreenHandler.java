package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.mixin.ServerPlayerAccesor;
import com.github.razorplay01.inv_view.util.ITargetPlayerContainer;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import com.github.razorplay01.inv_view.util.PermissionHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractInventoryScreenHandler extends AbstractContainerMenu implements ITargetPlayerContainer {
    protected final ServerPlayer targetPlayer;
    protected final UUID targetPlayerUUID;
    protected final InventoryLockManager.InventoryType lockType;
    protected final ServerPlayer viewer;
    protected final int inventorySize;
    protected final String interactPermission;

    protected AbstractInventoryScreenHandler(MenuType<?> type, int syncId, ServerPlayer viewer, ServerPlayer target, InventoryLockManager.InventoryType lockType, int inventorySize, String interactPermission) {
        super(type, syncId);
        this.targetPlayer = target;
        this.targetPlayerUUID = target.getUUID();
        this.lockType = lockType;
        this.viewer = viewer;
        this.inventorySize = inventorySize;
        this.interactPermission = interactPermission;
        if (!tryLockInventory(viewer)) {
            viewer.closeContainer();
        }
    }

    protected boolean tryLockInventory(ServerPlayer viewer) {
        if (!InventoryLockManager.tryLock(targetPlayerUUID, lockType)) {
            viewer.displayClientMessage(Component.translatable("inv_view_neoforge.command.error.inventory_in_use"), false);
            return false;
        }
        return true;
    }

    protected boolean canInteract() {
        if (interactPermission == null || interactPermission.isEmpty()) {
            return true;
        }
        return PermissionHandler.hasPermission(viewer.createCommandSourceStack(), interactPermission, 2);
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
            InvViewNeoforge.savePlayerData(((ServerPlayerAccesor) viewer).server(), targetPlayer);
        }
        // Liberar el bloqueo del inventario
        InventoryLockManager.unlock(targetPlayerUUID, lockType);
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!canInteract()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < inventorySize) {
                if (!this.moveItemStackTo(itemstack1, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
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