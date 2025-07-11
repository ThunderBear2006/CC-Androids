package com.thunderbear06.fabric.client;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.CCAndroidsClient;
import com.thunderbear06.entity.render.AndroidEntityRenderer;
import com.thunderbear06.entity.render.AndroidFrameEntityRenderer;
import com.thunderbear06.entity.render.RogueAndroidEntityRenderer;
import com.thunderbear06.entity.EntityRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class CCAndroidsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CCAndroidsClient.init();

		EntityRendererRegistry.register(EntityRegistry.ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.ADVANCED_ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.COMMAND_ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.ROGUE_ANDROID_ENTITY, (RogueAndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.ANDROID_FRAME_ENTITY, (AndroidFrameEntityRenderer::new));

		CCAndroids.LOGGER.info("Registered Forge client");
	}
}
