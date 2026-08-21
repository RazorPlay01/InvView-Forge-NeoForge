package com.github.razorplay01.inv_view.util;

import net.minecraft.server.level.ServerPlayer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

/**
 * Acceso a la API de Curios de forma compatible con todas las versiones
 * soportadas: las versiones modernas exponen {@code CuriosApi.getCuriosInventory},
 * mientras que Curios 5.x (1.19/1.20) utiliza {@code CuriosApi.getCuriosHelper()}.
 */
public final class CuriosAccess {

	private CuriosAccess() {
		// []
	}

	public static Optional<ICuriosItemHandler> inventory(ServerPlayer player) {
		//? >= 1.21.1 {
		return CuriosApi.getCuriosInventory(player);
		//?}
		//? < 1.21.1 {
		/*return CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve();
		*///?}
	}
}
