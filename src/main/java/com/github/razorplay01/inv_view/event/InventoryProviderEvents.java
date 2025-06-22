package com.github.razorplay01.inv_view.event;

import com.github.razorplay01.inv_view.InvViewNeoforge;
import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.api.InventoryProviderRegistry;
import com.github.razorplay01.inv_view.provider.CuriosCosmeticInventoryProvider;
import com.github.razorplay01.inv_view.provider.CuriosInventoryProvider;
import com.github.razorplay01.inv_view.provider.EnderChestProvider;
import com.github.razorplay01.inv_view.provider.PlayerInventoryProvider;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = InvViewNeoforge.MOD_ID)
public class InventoryProviderEvents {
    public static class RegisterInventoryProvidersEvent extends Event {
        private final List<Consumer<InventoryProvider>> listeners = new ArrayList<>();

        public void register(InventoryProvider provider) {
            InventoryProviderRegistry.register(provider);
        }

        public void addListener(Consumer<InventoryProvider> listener) {
            listeners.add(listener);
        }

        public void fire(InventoryProvider provider) {
            listeners.forEach(listener -> listener.accept(provider));
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        RegisterInventoryProvidersEvent registerEvent = new RegisterInventoryProvidersEvent();
        NeoForge.EVENT_BUS.post(registerEvent);
        registerEvent.register(new PlayerInventoryProvider());
        registerEvent.register(new EnderChestProvider());
        if (ModList.get().isLoaded("curios")) {
            registerEvent.register(new CuriosInventoryProvider());
            registerEvent.register(new CuriosCosmeticInventoryProvider());
        }
    }
}