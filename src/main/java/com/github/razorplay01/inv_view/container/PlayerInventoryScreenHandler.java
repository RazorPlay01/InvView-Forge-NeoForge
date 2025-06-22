package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
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
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = TARGET_INVENTORY_COLUMNS * TARGET_INVENTORY_ROWS;

    public PlayerInventoryScreenHandler(int syncId, ServerPlayer viewer, ServerPlayer target) {
        super(MenuType.GENERIC_9x5, syncId, viewer, target, InventoryLockManager.InventoryType.PLAYER_INVENTORY);
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            InvViewNeoforge.LOGGER.info("Invalid slotIndex:{}", index);
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        targetPlayer.getInventory().setChanged();
        return copyOfSourceStack;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (slotIndex >= 41 && slotIndex <= 44) {
            if (actionType == ClickType.QUICK_MOVE) {
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
}