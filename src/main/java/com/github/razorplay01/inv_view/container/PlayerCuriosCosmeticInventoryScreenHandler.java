package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import com.github.razorplay01.inv_view.util.ITargetPlayerContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerCuriosCosmeticInventoryScreenHandler extends AbstractInventoryScreenHandler implements ITargetPlayerContainer {
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;

    private final SimpleContainer curiosInv;
    private final List<Integer> validCuriosSlots = new ArrayList<>();
    private final int inventoryRows;
    private final int totalSlots;

    public PlayerCuriosCosmeticInventoryScreenHandler(MenuType<?> menuType, int syncId, ServerPlayer viewer, ServerPlayer target) {
        super(menuType, syncId, viewer, target, InventoryLockManager.InventoryType.CURIOS_COSMETIC, calculateInventoryRows(menuType) * 9);
        this.inventoryRows = calculateInventoryRows(menuType);
        this.totalSlots = inventoryRows * 9;
        this.curiosInv = new SimpleContainer(totalSlots);
        loadCuriosInventory();
        addInventorySlots();
    }

    private static int calculateInventoryRows(MenuType<?> menuType) {
        if (menuType == MenuType.GENERIC_9x1) return 1;
        else if (menuType == MenuType.GENERIC_9x2) return 2;
        else if (menuType == MenuType.GENERIC_9x3) return 3;
        else if (menuType == MenuType.GENERIC_9x4) return 4;
        else if (menuType == MenuType.GENERIC_9x5) return 5;
        else return 6;
    }

    private void loadCuriosInventory() {
        CuriosApi.getCuriosInventory(targetPlayer).ifPresent(curiosHandler -> {
            int index = 0;
            for (ICurioStacksHandler handler : curiosHandler.getCurios().values()) {
                for (int i = 0; i < handler.getCosmeticStacks().getSlots(); i++) {
                    if (index < totalSlots) {
                        ItemStack item = handler.getCosmeticStacks().getStackInSlot(i);
                        curiosInv.setItem(index, item);
                        validCuriosSlots.add(index);
                        index++;
                    } else {
                        break;
                    }
                }
                if (index >= totalSlots) break;
            }
        });
    }

    @Override
    protected void addInventorySlots() {
        // Añadir slots de Curios cosméticos
        for (int row = 0; row < inventoryRows; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(curiosInv, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Añadir slots del inventario del espectador
        int yOffset = (inventoryRows - 4) * 18;
        for (int row = 0; row < PLAYER_INVENTORY_ROW_COUNT; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMN_COUNT; col++) {
                this.addSlot(new Slot(viewer.getInventory(), col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yOffset));
            }
        }

        for (int col = 0; col < HOTBAR_SLOT_COUNT; col++) {
            this.addSlot(new Slot(viewer.getInventory(), col, 8 + col * 18, 161 + yOffset));
        }
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        super.clicked(slotIndex, button, actionType, player);
        // Guardar cambios en el inventario de Curios
        saveCuriosInventory();
        InvViewNeoforge.savePlayerData(targetPlayer);
        targetPlayer.inventoryMenu.sendAllDataToRemote();
    }

    @Override
    public void removed(@NotNull Player player) {
        saveCuriosInventory();
        super.removed(player);
    }

    private void saveCuriosInventory() {
        CuriosApi.getCuriosInventory(targetPlayer).ifPresent(curiosHandler -> {
            int slotIndex = 0;
            for (ICurioStacksHandler handler : curiosHandler.getCurios().values()) {
                for (int i = 0; i < handler.getCosmeticStacks().getSlots(); i++) {
                    if (slotIndex < totalSlots) {
                        ItemStack itemStack = curiosInv.getItem(slotIndex);
                        handler.getCosmeticStacks().setStackInSlot(i, itemStack);
                        slotIndex++;
                    } else {
                        break;
                    }
                }
            }
        });
    }
}