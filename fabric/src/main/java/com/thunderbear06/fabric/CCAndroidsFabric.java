package com.thunderbear06.fabric;

import com.thunderbear06.CCAndroids;
import net.fabricmc.api.ModInitializer;

public final class CCAndroidsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		AndroidPlatformHelperFabric.init();

		// Run our common setup.
		CCAndroids.init();
	}
}
