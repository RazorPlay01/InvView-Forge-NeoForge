package com.github.razorplay01.inv_view;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;

import java.io.File;
import java.io.FileOutputStream;

@Mod(InvViewNeoforge.MOD_ID)
public class InvViewNeoforge {
    public static final String MOD_ID = "inv_view_neoforge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InvViewNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("InvView Neoforge initialized.");
    }

    public static void savePlayerData(ServerPlayer player) {
        File playerDataDir = player.server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();

        try {
            CompoundTag compoundTag = player.saveWithoutId(new CompoundTag());
            File file = File.createTempFile(player.getStringUUID() + "-", ".dat", playerDataDir);
            final FileOutputStream fos = new FileOutputStream(file);
            NbtIo.writeCompressed(compoundTag, fos);
            File file2 = new File(playerDataDir, player.getStringUUID() + ".dat");
            File file3 = new File(playerDataDir, player.getStringUUID() + ".dat_old");
            Util.safeReplaceFile(file2.toPath(), file.toPath(), file3.toPath());
        } catch (Exception var6) {
            LOGGER.warn("Failed to save player data for {}", player.getName().getString());
        }
    }
}
