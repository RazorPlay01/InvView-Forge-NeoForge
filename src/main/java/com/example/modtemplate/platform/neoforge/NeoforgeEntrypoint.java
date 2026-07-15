package com.example.modtemplate.platform.neoforge;

//? neoforge {

import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.event.InventoryProviderEvents;
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
