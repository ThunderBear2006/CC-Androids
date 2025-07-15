package com.thunderbear06.forge;

import com.thunderbear06.CCAndroids;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CCAndroids.MOD_ID)
public final class CCAndroidsForge {
	public CCAndroidsForge() {
		AndroidPlatformHelperForge.init();

		// Submit our event bus to let Architectury API register our content on the right time.
		EventBuses.registerModEventBus(CCAndroids.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

		// Run our common setup.
		CCAndroids.init();
	}
}
