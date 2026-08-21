package com.github.razorplay01.inv_view.provider;

import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.container.PlayerCuriosInventoryScreenHandler;
import com.github.razorplay01.inv_view.util.CuriosAccess;
import com.github.razorplay01.inv_view.util.InventoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CuriosInventoryProvider implements InventoryProvider {

	@Override
	public String getId() {
		return "curios";
	}

	@Override
	public boolean isAvailable(ServerPlayer target) {
		return CuriosAccess.inventory(target)
				.map(handler -> handler.getCurios().values().stream()
						.anyMatch(stackHandler -> stackHandler.getSlots() > 0))
				.orElse(false);
	}

	@Override
	public InventoryType getLockType() {
		return InventoryType.CURIOS;
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target) {
		return new PlayerCuriosInventoryScreenHandler(syncId, viewer, target, false, getInteractPermission());
	}

	@Override
	public Component getDisplayName(ServerPlayer target) {
		return Component.translatable("inv_view_neoforge.curios.inventory", target.getDisplayName());
	}

	@Override
	public String getOpenPermission() {
		return "inv_view.curios";
	}

	@Override
	public String getInteractPermission() {
		return "inv_view.interact.curios";
	}
}
