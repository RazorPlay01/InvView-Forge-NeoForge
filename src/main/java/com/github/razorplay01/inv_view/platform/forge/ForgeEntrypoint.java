package com.github.razorplay01.inv_view.platform.forge;

//? forge {

/*import com.github.razorplay01.inv_view.ModTemplate;
import com.github.razorplay01.inv_view.event.InventoryProviderEvents;
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
