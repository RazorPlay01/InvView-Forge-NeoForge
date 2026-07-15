package com.example.modtemplate.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InventoryProviderRegistry {
	private InventoryProviderRegistry() {
		// []
	}

	private static final Map<String, InventoryProvider> PROVIDERS = new HashMap<>();

	public static void register(InventoryProvider provider) {
		PROVIDERS.put(provider.getId(), provider);
	}

	public static InventoryProvider getProvider(String id) {
		return PROVIDERS.get(id);
	}

	public static Collection<InventoryProvider> getAllProviders() {
		return PROVIDERS.values();
	}
}
