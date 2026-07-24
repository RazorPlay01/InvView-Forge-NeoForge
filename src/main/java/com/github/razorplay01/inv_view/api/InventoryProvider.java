package com.github.razorplay01.inv_view.api;

import com.github.razorplay01.inv_view.util.InventoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface InventoryProvider {
	String getId();
	boolean isAvailable(ServerPlayer target);
	InventoryType getLockType();
	AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target);
	Component getDisplayName(ServerPlayer target);
	String getOpenPermission();
	String getInteractPermission();
}
