package com.thunderbear06;

import com.thunderbear06.menu.MenuRegistry;
import com.thunderbear06.screen.AndroidScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class CCAndroidsClient {

	public static void init() {
		// some stuff moved to CCAndroidsFabricClient.java and CCAndroidsForgeClient.java
		HandledScreens.register(MenuRegistry.ANDROID.get(), AndroidScreen::new);
	}
}