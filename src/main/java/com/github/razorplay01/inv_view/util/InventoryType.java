package com.github.razorplay01.inv_view.util;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tipo de inventario extensible. Los desarrolladores pueden registrar nuevos tipos
 * usando {@link #register(String, InventoryType)} o creando instancias directamente.
 * Se recomienda usar identificadores únicos (ej. "mod:player_inventory").
 */
public final class InventoryType {
	private final String id;

	// Registry interno (público para consulta, pero la modificación es controlada)
	private static final ConcurrentHashMap<String, InventoryType> REGISTRY = new ConcurrentHashMap<>();

	// Tipos predefinidos (para compatibilidad con el código existente)
	public static final InventoryType PLAYER_INVENTORY = register("player_inventory", new InventoryType("player_inventory"));
	public static final InventoryType ENDER_CHEST = register("ender_chest", new InventoryType("ender_chest"));

	public InventoryType(String id) {
		this.id = Objects.requireNonNull(id, "id cannot be null");
	}

	/**
	 * Registra un nuevo tipo de inventario. Si ya existe un tipo con el mismo id,
	 * se sobrescribe (o se puede lanzar excepción, según prefieras).
	 * @param id Identificador único del tipo
	 * @param type Instancia de InventoryType
	 * @return el tipo registrado (para encadenar)
	 * @throws IllegalArgumentException si id es null o vacío
	 */
	public static InventoryType register(String id, InventoryType type) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("id cannot be null or empty");
		}
		REGISTRY.put(id, type);
		return type;
	}

	/**
	 * Obtiene un tipo registrado por su id.
	 * @return el InventoryType, o null si no existe
	 */
	public static InventoryType get(String id) {
		return REGISTRY.get(id);
	}

	/**
	 * Obtiene todos los tipos registrados.
	 */
	public static Collection<InventoryType> values() {
		return REGISTRY.values();
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InventoryType that)) return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "InventoryType{" + id + "}";
	}
}
