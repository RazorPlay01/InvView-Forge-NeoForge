package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.util.InventoryType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PlayerInventoryScreenHandler extends AbstractInventoryScreenHandler {
	private static final int TARGET_INVENTORY_ROWS = 5;
	private static final int TARGET_INVENTORY_COLUMNS = 9;
	private static final int PLAYER_INVENTORY_ROWS = 3;
	private static final int PLAYER_INVENTORY_COLUMNS = 9;
	private static final int SLOT_SIZE = 18;
	private static final int PLAYER_INVENTORY_Y_OFFSET = 103;
	private static final int HOTBAR_Y_OFFSET = 161;

	private static final int HOTBAR_SLOT_COUNT = 9;

	public PlayerInventoryScreenHandler(int syncId, ServerPlayer viewer, ServerPlayer target, String interactPermission) {
		super(MenuType.GENERIC_9x5, syncId, viewer, target, InventoryType.PLAYER_INVENTORY, 9 * 5, interactPermission);
		addInventorySlots();
	}

	@Override
	protected void addInventorySlots() {
		// Añadir slots del inventario del objetivo
		for (int row = 0; row < TARGET_INVENTORY_ROWS; ++row) {
			for (int column = 0; column < TARGET_INVENTORY_COLUMNS; ++column) {
				this.addSlot(new Slot(targetPlayer.getInventory(), column + row * TARGET_INVENTORY_COLUMNS, 8 + column * SLOT_SIZE, 18 + row * SLOT_SIZE));
			}
		}

		// Añadir slots del inventario del espectador
		for (int row = 0; row < PLAYER_INVENTORY_ROWS; ++row) {
			for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; ++column) {
				this.addSlot(new Slot(viewer.getInventory(), column + row * PLAYER_INVENTORY_COLUMNS + 9, 8 + column * SLOT_SIZE, PLAYER_INVENTORY_Y_OFFSET + row * SLOT_SIZE));
			}
		}

		for (int column = 0; column < HOTBAR_SLOT_COUNT; ++column) {
			this.addSlot(new Slot(viewer.getInventory(), column, 8 + column * SLOT_SIZE, HOTBAR_Y_OFFSET));
		}
	}

	//? >= 26 {
	@Override
	public void clicked(int slotIndex, int buttonNum, net.minecraft.world.inventory.ContainerInput containerInput, Player player) {
		if (slotIndex >= 41 && slotIndex <= 44) {
			if (containerInput == net.minecraft.world.inventory.ContainerInput.QUICK_MOVE) {
				super.clicked(slotIndex, buttonNum, containerInput, player);
			} else {
				player.getInventory().setItem(slotIndex, ItemStack.EMPTY);
			}
		} else {
			super.clicked(slotIndex, buttonNum, containerInput, player);
		}
		targetPlayer.getInventory().setChanged();
		targetPlayer.inventoryMenu.sendAllDataToRemote();
	}
	//?}
	//? < 26 {
	/*@Override
	public void clicked(int slotIndex, int button, net.minecraft.world.inventory.ClickType actionType, Player player) {
		if (slotIndex >= 41 && slotIndex <= 44) {
			if (actionType == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
				super.clicked(slotIndex, button, actionType, player);
			} else {
				player.getInventory().setItem(slotIndex, ItemStack.EMPTY);
			}
		} else {
			super.clicked(slotIndex, button, actionType, player);
		}
		targetPlayer.getInventory().setChanged();
		targetPlayer.inventoryMenu.sendAllDataToRemote();
	}
	*///?}
}
