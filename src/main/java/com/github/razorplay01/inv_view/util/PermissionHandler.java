package com.github.razorplay01.inv_view.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
//? neoforge {
import net.neoforged.fml.ModList;
//?} forge {
/*import net.minecraftforge.fml.ModList;
*///?}

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
			//? <= 1.21.10{
			/*return source.hasPermission(permissionLevel);
			*///?}
			//? > 1.21.10{
			net.minecraft.server.permissions.Permission levelPerm = new net.minecraft.server.permissions.Permission.HasCommandLevel(net.minecraft.server.permissions.PermissionLevel.byId(permissionLevel));
			return source.permissions().hasPermission(levelPerm);
			//?}
		}

		if (IS_LUCKPERMS_LOADED && luckPermsApi != null) {
			var user = luckPermsApi.getPlayerAdapter(ServerPlayer.class).getUser(player);
			return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
		}

		//? <= 1.21.10{
		/*return source.hasPermission(permissionLevel);
		*///?}
		//? > 1.21.10{
		net.minecraft.server.permissions.Permission levelPerm = new net.minecraft.server.permissions.Permission.HasCommandLevel(net.minecraft.server.permissions.PermissionLevel.byId(permissionLevel));
		return source.permissions().hasPermission(levelPerm);
		//?}
	}
}
