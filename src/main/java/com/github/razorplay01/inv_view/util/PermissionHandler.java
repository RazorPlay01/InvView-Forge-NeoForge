package com.github.razorplay01.inv_view.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class PermissionHandler {
    private static final boolean IS_LUCKPERMS_LOADED = ModList.get().isLoaded("luckperms");
    @Nullable
    private static LuckPerms luckPermsApi = null;

    static {
        if (IS_LUCKPERMS_LOADED) {
            try {
                luckPermsApi = LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                luckPermsApi = null;
            }
        }
    }

    public static boolean hasPermission(CommandSourceStack source, String permission, int permissionLevel) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            // Para comandos ejecutados por consola o bloques de comando, usar nivel de operador
            Permission levelPerm = new Permission.HasCommandLevel(PermissionLevel.byId(permissionLevel));
            return source.permissions().hasPermission(levelPerm);
        }

        if (IS_LUCKPERMS_LOADED && luckPermsApi != null) {
            var user = luckPermsApi.getPlayerAdapter(ServerPlayer.class).getUser(player);
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        }

        // Fallback al sistema de permisos de Minecraft
        Permission levelPerm = new Permission.HasCommandLevel(PermissionLevel.byId(permissionLevel));
        return source.permissions().hasPermission(levelPerm);
    }
}
