package com.example.modtemplate.provider;

import com.example.modtemplate.api.InventoryProvider;
import com.example.modtemplate.container.PlayerEnderChestScreenHandler;
import com.example.modtemplate.util.InventoryLockManager;
import com.example.modtemplate.util.InventoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class EnderChestProvider implements InventoryProvider {
	@Override
	public String getId() {
		return "echest";
	}

	@Override
	public boolean isAvailable(ServerPlayer target) {
		return true; // El Ender Chest siempre está disponible
	}

	@Override
	public InventoryType getLockType() {
		return InventoryType.ENDER_CHEST;
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target) {
		return new PlayerEnderChestScreenHandler(syncId, viewer, target, getInteractPermission());
	}

	@Override
	public Component getDisplayName(ServerPlayer target) {
		return Component.translatable("container.enderchest", target.getDisplayName());
	}

	@Override
	public String getOpenPermission() {
		return "inv_view.echest";
	}

	@Override
	public String getInteractPermission() {
		return "inv_view.interact.echest";
	}
}
