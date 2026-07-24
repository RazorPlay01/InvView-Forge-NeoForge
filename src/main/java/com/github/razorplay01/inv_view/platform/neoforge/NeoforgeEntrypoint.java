package com.github.razorplay01.inv_view.platform.neoforge;

//? neoforge {

import com.github.razorplay01.inv_view.ModTemplate;
import com.github.razorplay01.inv_view.event.InventoryProviderEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModTemplate.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modEventBus) {
		ModTemplate.onInitialize();
		modEventBus.addListener(InventoryProviderEvents::registerProviders);
	}
}
//?}
