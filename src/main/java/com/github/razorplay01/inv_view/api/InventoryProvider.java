package com.github.razorplay01.inv_view.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.github.razorplay01.inv_view.util.InventoryLockManager;

public interface InventoryProvider {
    String getId();
    boolean isAvailable(ServerPlayer target);
    InventoryLockManager.InventoryType getLockType();
    AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target);
    Component getDisplayName(ServerPlayer target);
    String getOpenPermission();
    String getInteractPermission();
}
