package com.thunderbear06.mixin;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(at = @At("TAIL"), method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V")
    private void broadcastToAndroids(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        sender.getServerWorld().getEntitiesByClass(BaseAndroidEntity.class, sender.getBoundingBox().expand(50), baseAndroidEntity -> true).forEach(baseAndroidEntity -> {
            baseAndroidEntity.readChatMessage(message.getContent().getString(), params.name().getString(), sender.getUuid());
        });
    }
}
