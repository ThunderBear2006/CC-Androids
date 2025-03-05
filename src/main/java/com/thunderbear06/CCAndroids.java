package com.thunderbear06;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.api.AndroidAPI;
import com.thunderbear06.config.CCAndroidsConfig;
import com.thunderbear06.config.ConfigLoader;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.menu.MenuRegistry;
import com.thunderbear06.sounds.SoundRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCAndroids implements ModInitializer {
	public static final String MOD_ID = "cc-androids";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static CCAndroidsConfig CONFIG;

	@Override
	public void onInitialize() {
		ComputerCraftAPI.registerAPIFactory(computer -> {
			AndroidBrain brain = computer.getComponent(ComputerComponents.ANDROID_COMPUTER);
			return brain == null ? null : new AndroidAPI(brain);
		});

		CONFIG = ConfigLoader.loadConfig(MOD_ID, new CCAndroidsConfig());
		LOGGER.info("Loaded Config File");

		MenuRegistry.register();
		ItemRegistry.register();
		EntityRegistry.register();
		SoundRegistry.register();
	}
}