package com.github.razorplay01.inv_view;

import com.github.razorplay01.inv_view.event.InventoryProviderEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(InvViewNeoforge.MOD_ID)
public class InvViewNeoforge {
    public static final String MOD_ID = "inv_view_neoforge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InvViewNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(InventoryProviderEvents::registerProviders);
        LOGGER.info("InvView Neoforge initialized.");
    }


    public static void savePlayerData(MinecraftServer server, ServerPlayer player) {
        File playerDataDir = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(player.problemPath(), LogUtils.getLogger())) {
            TagValueOutput nbtWriteView = TagValueOutput.createWithContext(logging, player.registryAccess());
            player.saveWithoutId(nbtWriteView);
            Path path = playerDataDir.toPath();
            Path path2 = Files.createTempFile(path, player.getStringUUID() + "-", ".dat");
            CompoundTag nbtCompound = nbtWriteView.buildResult();
            NbtIo.writeCompressed(nbtCompound, path2);
            Path path3 = path.resolve(player.getStringUUID() + ".dat");
            Path path4 = path.resolve(player.getStringUUID() + ".dat_old");
            Util.safeReplaceFile(path3, path2, path4);
        } catch (Exception var11) {
            LogUtils.getLogger().warn("Failed to save player data for {}", player.getName().getString());
        }
    }
}
