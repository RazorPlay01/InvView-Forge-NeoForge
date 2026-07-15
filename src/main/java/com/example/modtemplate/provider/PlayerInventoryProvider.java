package com.example.modtemplate.provider;

import com.example.modtemplate.api.InventoryProvider;
import com.example.modtemplate.container.PlayerInventoryScreenHandler;
import com.example.modtemplate.util.InventoryLockManager;
import com.example.modtemplate.util.InventoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerInventoryProvider implements InventoryProvider {
	@Override
	public String getId() {
		return "inv";
	}

	@Override
	public boolean isAvailable(ServerPlayer target) {
		return true; // El inventario siempre está disponible
	}

	@Override
	public InventoryType getLockType() {
		return InventoryType.PLAYER_INVENTORY;
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target) {
		return new PlayerInventoryScreenHandler(syncId, viewer, target,getInteractPermission());
	}

	@Override
	public Component getDisplayName(ServerPlayer target) {
		return target.getDisplayName();
	}

	@Override
	public String getOpenPermission() {
		return "inv_view.inv";
	}

	@Override
	public String getInteractPermission() {
		return "inv_view.interact.inv";
	}
}
