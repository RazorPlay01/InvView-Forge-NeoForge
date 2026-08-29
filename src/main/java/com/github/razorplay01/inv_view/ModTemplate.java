package com.github.razorplay01.inv_view;

import com.github.razorplay01.inv_view.platform.Platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
/*import com.github.razorplay01.inv_view.platform.fabric.FabricPlatform;
 *///?} neoforge {
import com.github.razorplay01.inv_view.platform.neoforge.NeoforgePlatform;
 //?} forge {
/*import com.github.razorplay01.inv_view.platform.forge.ForgePlatform;
*///?}
import java.io.File;
//? <1.21.10{
/*import net.minecraft.Util;
*///?}
//? >=1.21.10{
import net.minecraft.util.Util;
//?}

@SuppressWarnings("LoggingSimilarMessage")
public class ModTemplate {

	public static final String MOD_ID = /*$ mod_id*/ "inv_view";
	public static final String MOD_VERSION = /*$ mod_version*/ "4.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Inv View";
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

	public static Platform xplat() {
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

	//? <=1.21.1{
	/*public static void savePlayerData(MinecraftServer server, ServerPlayer player) {
		File playerDataDir = player.server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
		try {
			net.minecraft.nbt.CompoundTag compoundTag = player.saveWithoutId(new net.minecraft.nbt.CompoundTag());
			File file = File.createTempFile(player.getStringUUID() + "-", ".dat", playerDataDir);
			final java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
			net.minecraft.nbt.NbtIo.writeCompressed(compoundTag, fos);
			File file2 = new File(playerDataDir, player.getStringUUID() + ".dat");
			File file3 = new File(playerDataDir, player.getStringUUID() + ".dat_old");
			Util.safeReplaceFile(file2.toPath(), file.toPath(), file3.toPath());
		} catch (Exception var6) {
			LOGGER.warn("Failed to save player data for {}", player.getName().getString());
		}
	}
	*///?}

	//? >1.21.1{
	public static void savePlayerData(MinecraftServer server, ServerPlayer player) {
		File playerDataDir = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
		try (net.minecraft.util.ProblemReporter.ScopedCollector logging = new net.minecraft.util.ProblemReporter.ScopedCollector(player.problemPath(), LOGGER)) {
			net.minecraft.world.level.storage.TagValueOutput nbtWriteView = net.minecraft.world.level.storage.TagValueOutput.createWithContext(logging, player.registryAccess());
			player.saveWithoutId(nbtWriteView);
			java.nio.file.Path path = playerDataDir.toPath();
			java.nio.file.Path path2 = java.nio.file.Files.createTempFile(path, player.getStringUUID() + "-", ".dat");
			net.minecraft.nbt.CompoundTag nbtCompound = nbtWriteView.buildResult();
			net.minecraft.nbt.NbtIo.writeCompressed(nbtCompound, path2);
			java.nio.file.Path path3 = path.resolve(player.getStringUUID() + ".dat");
			java.nio.file.Path path4 = path.resolve(player.getStringUUID() + ".dat_old");
			Util.safeReplaceFile(path3, path2, path4);
		} catch (Exception var11) {
			LOGGER.warn("Failed to save player data for {}", player.getName().getString());
		}
	}
	//?}
}
