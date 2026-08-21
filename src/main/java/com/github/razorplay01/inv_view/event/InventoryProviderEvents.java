package com.github.razorplay01.inv_view.event;

import com.github.razorplay01.inv_view.ModTemplate;
import com.github.razorplay01.inv_view.api.InventoryProvider;
import com.github.razorplay01.inv_view.api.InventoryProviderRegistry;
import com.github.razorplay01.inv_view.provider.CuriosCosmeticInventoryProvider;
import com.github.razorplay01.inv_view.provider.CuriosInventoryProvider;
import com.github.razorplay01.inv_view.provider.EnderChestProvider;
import com.github.razorplay01.inv_view.provider.PlayerInventoryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//? neoforge {
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
*///?}

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
		if (ModTemplate.xplat().isModLoaded("curios")) {
			registerEvent.register(new CuriosInventoryProvider());
			registerEvent.register(new CuriosCosmeticInventoryProvider());
		}
	}
}
