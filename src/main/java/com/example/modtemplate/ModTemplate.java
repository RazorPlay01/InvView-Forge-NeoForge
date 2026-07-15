package com.example.modtemplate;

import com.example.modtemplate.platform.Platform;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
/*import com.example.modtemplate.platform.fabric.FabricPlatform;
*///?} neoforge {
import com.example.modtemplate.platform.neoforge.NeoforgePlatform;
 //?} forge {
/*import com.example.modtemplate.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ModTemplate {

	public static final String MOD_ID = /*$ mod_id*/ "modtemplate";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Mod Template";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, ModTemplate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, ModTemplate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		/*return new FabricPlatform();
		*///?} neoforge {
		return new NeoforgePlatform();
		 //?} forge {
		/*return new ForgePlatform();
		 *///?}
	}

	public static void savePlayerData(net.minecraft.server.MinecraftServer server, net.minecraft.server.level.ServerPlayer player) {
		java.io.File playerDataDir = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.PLAYER_DATA_DIR).toFile();
		try (net.minecraft.util.ProblemReporter.ScopedCollector logging = new net.minecraft.util.ProblemReporter.ScopedCollector(player.problemPath(), LOGGER)) {
			net.minecraft.world.level.storage.TagValueOutput nbtWriteView = net.minecraft.world.level.storage.TagValueOutput.createWithContext(logging, player.registryAccess());
			player.saveWithoutId(nbtWriteView);
			java.nio.file.Path path = playerDataDir.toPath();
			java.nio.file.Path path2 = java.nio.file.Files.createTempFile(path, player.getStringUUID() + "-", ".dat");
			net.minecraft.nbt.CompoundTag nbtCompound = nbtWriteView.buildResult();
			net.minecraft.nbt.NbtIo.writeCompressed(nbtCompound, path2);
			java.nio.file.Path path3 = path.resolve(player.getStringUUID() + ".dat");
			java.nio.file.Path path4 = path.resolve(player.getStringUUID() + ".dat_old");
			net.minecraft.util.Util.safeReplaceFile(path3, path2, path4);
		} catch (Exception var11) {
			LOGGER.warn("Failed to save player data for {}", player.getName().getString());
		}
	}
}
