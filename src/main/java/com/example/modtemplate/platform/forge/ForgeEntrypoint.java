package com.example.modtemplate.platform.forge;

//? forge {

/*import com.example.modtemplate.ModTemplate;
import com.example.modtemplate.event.InventoryProviderEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModTemplate.MOD_ID)
public class ForgeEntrypoint {

	public ForgeEntrypoint(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		ModTemplate.onInitialize();
		modEventBus.addListener(InventoryProviderEvents::registerProviders);
	}
}
*///?}
