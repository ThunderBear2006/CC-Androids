package com.thunderbear06.entity.render;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class AndroidEmissiveRenderer extends FeatureRenderer<AndroidEntity, PlayerEntityModel<AndroidEntity>> {

    public AndroidEmissiveRenderer(FeatureRendererContext<AndroidEntity, PlayerEntityModel<AndroidEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AndroidEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        RenderLayer renderLayer = this.getEyesTexture(entity);
        if (renderLayer == null)
            return;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        Vector3f color = getColor(entity).toVector3f();
        this.getContextModel().render(matrices, vertexConsumer, 1, OverlayTexture.DEFAULT_UV, color.x, color.y, color.z, 1.0F);
    }

    public abstract @Nullable RenderLayer getEyesTexture(AndroidEntity entity);

    public Vec3d getColor(AndroidEntity entity) {
        return new Vec3d(1,1,1);
    };
}
