package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.util.InventoryLockManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class PlayerEnderChestScreenHandler extends AbstractInventoryScreenHandler {
    private int rows;

    public PlayerEnderChestScreenHandler(int syncId, ServerPlayer viewer, ServerPlayer target, String interactPermission) {
        super(getMenuType(target.getEnderChestInventory().getContainerSize()), syncId, viewer, target, InventoryLockManager.InventoryType.ENDER_CHEST, target.getEnderChestInventory().getContainerSize(), interactPermission);
        initializeInventorySize();
        addInventorySlots();
    }

    private void initializeInventorySize() {
        int containerSize = targetPlayer.getEnderChestInventory().getContainerSize();
        rows = switch (containerSize) {
            case 9 -> 1;
            case 18 -> 2;
            case 36 -> 4;
            case 45 -> 5;
            case 54 -> 6;
            default -> 3;
        };
    }

    @Override
    protected void addInventorySlots() {
        // Añadir slots del Ender Chest
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(targetPlayer.getEnderChestInventory(), col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Añadir slots del inventario del espectador
        int yOffset = (rows - 4) * 18;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(viewer.getInventory(), col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yOffset));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(viewer.getInventory(), col, 8 + col * 18, 161 + yOffset));
        }
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        super.clicked(slotIndex, button, actionType, player);
        // Marcar Ender Chest como modificado y sincronizar
        targetPlayer.getEnderChestInventory().setChanged();
        targetPlayer.inventoryMenu.sendAllDataToRemote();
    }

    private static MenuType<?> getMenuType(int size) {
        if (size >= 0 && size <= 9) {
            return MenuType.GENERIC_9x1;
        } else if (size > 9 && size <= 18) {
            return MenuType.GENERIC_9x2;
        } else if (size > 18 && size <= 27) {
            return MenuType.GENERIC_9x3;
        } else if (size > 27 && size <= 36) {
            return MenuType.GENERIC_9x4;
        } else if (size > 36 && size <= 45) {
            return MenuType.GENERIC_9x5;
        } else {
            return MenuType.GENERIC_9x6;
        }
    }
}