package com.github.razorplay01.inv_view.provider;

import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.container.PlayerCuriosCosmeticInventoryScreenHandler;
import com.github.razorplay01.inv_view.util.InventoryLockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class CuriosCosmeticInventoryProvider implements InventoryProvider {
    @Override
    public String getId() {
        return "curios_cosmetic";
    }

    @Override
    public boolean isAvailable(ServerPlayer target) {
        return CuriosApi.getCuriosInventory(target).isPresent();
    }

    @Override
    public InventoryLockManager.InventoryType getLockType() {
        return InventoryLockManager.InventoryType.CURIOS_COSMETIC;
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, ServerPlayer viewer, ServerPlayer target) {
        AtomicInteger slotCount = new AtomicInteger();
        CuriosApi.getCuriosInventory(target).ifPresent(curiosHandler -> {
            for (ICurioStacksHandler handler : curiosHandler.getCurios().values()) {
                slotCount.addAndGet(handler.getCosmeticStacks().getSlots());
            }
        });
        MenuType<?> menuType = getMenuType(slotCount.get());
        return new PlayerCuriosCosmeticInventoryScreenHandler(menuType, syncId, viewer, target);
    }

    @Override
    public Component getDisplayName(ServerPlayer target) {
        return Component.translatable("inv_view_neoforge.curios.cosmetic_inventory", target.getDisplayName());
    }

    @Override
    public String getPermission() {
        return "inv_view.curios_cosmetic";
    }

    private MenuType<?> getMenuType(int size) {
        if (size <= 9) return MenuType.GENERIC_9x1;
        else if (size <= 18) return MenuType.GENERIC_9x2;
        else if (size <= 27) return MenuType.GENERIC_9x3;
        else if (size <= 36) return MenuType.GENERIC_9x4;
        else if (size <= 45) return MenuType.GENERIC_9x5;
        else return MenuType.GENERIC_9x6;
    }
}