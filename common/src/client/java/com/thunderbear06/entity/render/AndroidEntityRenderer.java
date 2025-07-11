package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class AndroidEntityRenderer extends BipedEntityRenderer<AndroidEntity, PlayerEntityModel<AndroidEntity>> {
    private final Identifier androidNormal = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private final Identifier androidAdvanced = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private final Identifier androidCommand = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_command.png");
    private final Identifier core_emissive = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/core.png");

    private final Identifier face_anger = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/anger.png");
    private final Identifier face_annoyed = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/annoyed.png");
    private final Identifier face_command = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/command.png");
    private final Identifier face_happy = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/happy.png");
    private final Identifier face_normal = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/normal.png");
    private final Identifier face_sad = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/sad.png");
    private final Identifier face_woozy = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/face/woozy.png");

    public AndroidEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);

        this.addFeature(new AndroidEmissiveRenderer(this) {
            @Override
            public RenderLayer getEyesTexture(AndroidEntity entity) {
                if (entity.hasVariant())
                    return RenderLayer.getEyes(getVariantTexture(entity.getVariant(), true));

                return RenderLayer.getEyes(core_emissive);
            }
        });

        this.addFeature(new AndroidEmissiveRenderer(this) {
            @Override
            public RenderLayer getEyesTexture(AndroidEntity entity) {
                if (entity.hasVariant())
                    return null;

                return RenderLayer.getEyes(switch (entity.getFace()) {
                    case 0 -> {
                        if (entity instanceof CommandAndroidEntity)
                            yield face_command;
                        else
                            yield face_normal;
                    }
                    case 1 -> face_anger;
                    case 2 -> face_annoyed;
                    case 3 -> face_happy;
                    case 4 -> face_sad;
                    case 5 -> face_woozy;
                    default -> null;
                });
            }

            @Override
            public Vec3d getColor(AndroidEntity entity) {
                return switch (entity.getComputer().getFamily()) {
                    case NORMAL -> new Vec3d(1,1,1);
                    case ADVANCED -> new Vec3d(1.5, 1,1);
                    case COMMAND -> new Vec3d(1,0,0);
                };
            }
        });
    }

    @Override
    public Identifier getTexture(AndroidEntity entity) {
        if (entity.hasVariant())
            return getVariantTexture(entity.getVariant(), false);

        ComputerFamily family = entity.getComputer().family;
        if (family == ComputerFamily.ADVANCED)
            return androidAdvanced;
        if (family == ComputerFamily.COMMAND)
            return androidCommand;
        return androidNormal;
    }

    private Identifier getVariantTexture(byte b, boolean emissive) {
        String path = emissive ? "textures/entity/emissive/variant/" : "textures/entity/variant/";

        String name = switch (b) {
            case 1 -> "android_kaylon.png";
            case 2 -> "android_pinky.png";
            default -> throw new IllegalArgumentException();
        };

        return new Identifier(CCAndroids.MOD_ID,path+name);
    }
}
