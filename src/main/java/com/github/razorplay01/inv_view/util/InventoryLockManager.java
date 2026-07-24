package com.github.razorplay01.inv_view.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryLockManager {
	private static final Map<InventoryType, Set<UUID>> lockedInventories = new ConcurrentHashMap<>();

	static {
		// Inicializar los Sets para cada tipo de inventario
		for (InventoryType type : InventoryType.values()) {
			lockedInventories.put(type, Collections.synchronizedSet(new HashSet<>()));
		}
	}

	private static Set<UUID> getOrCreateLockSet(InventoryType type) {
		return lockedInventories.computeIfAbsent(type, k -> Collections.synchronizedSet(new HashSet<>()));
	}

	public static boolean tryLock(UUID playerUUID, InventoryType type) {
		Set<UUID> locks = getOrCreateLockSet(type);
		if (locks.contains(playerUUID)) {
			return false;
		}
		return locks.add(playerUUID);
	}

	public static void unlock(UUID playerUUID, InventoryType type) {
		Set<UUID> locks = lockedInventories.get(type);
		if (locks != null) {
			locks.remove(playerUUID);
		}
	}

	public static boolean isLocked(UUID playerUUID, InventoryType type) {
		Set<UUID> locks = lockedInventories.get(type);
		return locks != null && locks.contains(playerUUID);
	}

	public static boolean hasAnyLock(UUID playerUUID) {
		return lockedInventories.values().stream().anyMatch(set -> set.contains(playerUUID));
	}

	public static void unlockAll(UUID playerUUID) {
		lockedInventories.values().forEach(set -> set.remove(playerUUID));
	}

	public static Optional<UUID> getLocker(InventoryType type) {
		Set<UUID> locks = lockedInventories.get(type);
		if (locks == null || locks.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(locks.iterator().next());
	}

	public static boolean isInventoryLocked(InventoryType type) {
		Set<UUID> locks = lockedInventories.get(type);
		return locks != null && !locks.isEmpty();
	}

	public static void clearAllLocks() {
		lockedInventories.values().forEach(Set::clear);
	}
}
