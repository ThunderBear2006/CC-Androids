package com.thunderbear06.forge;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.CCAndroidsClient;
import com.thunderbear06.entity.render.AndroidEntityRenderer;
import com.thunderbear06.entity.render.AndroidFrameEntityRenderer;
import com.thunderbear06.entity.render.RogueAndroidEntityRenderer;
import com.thunderbear06.entity.EntityRegistry;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CCAndroids.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCAndroidsForgeClient {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CCAndroidsClient.init();

		EntityRenderers.register(EntityRegistry.ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ADVANCED_ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.COMMAND_ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ROGUE_ANDROID_ENTITY.get(), (RogueAndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ANDROID_FRAME_ENTITY.get(), (AndroidFrameEntityRenderer::new));

		CCAndroids.LOGGER.info("Registered Forge client");
	}
}
