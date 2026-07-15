package com.example.modtemplate.event;

import com.example.modtemplate.api.InventoryProvider;
import com.example.modtemplate.api.InventoryProviderRegistry;
import com.example.modtemplate.provider.EnderChestProvider;
import com.example.modtemplate.provider.PlayerInventoryProvider;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	public static void registerProviders(FMLCommonSetupEvent event) {
		RegisterInventoryProvidersEvent registerEvent = new RegisterInventoryProvidersEvent();
		registerEvent.register(new PlayerInventoryProvider());
		registerEvent.register(new EnderChestProvider());
        /*if (ModList.get().isLoaded("curios")) {
            registerEvent.register(new CuriosInventoryProvider());
            registerEvent.register(new CuriosCosmeticInventoryProvider());
        }*/
	}
}
