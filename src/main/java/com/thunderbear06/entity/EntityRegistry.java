package com.thunderbear06.entity;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.*;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;

public class EntityRegistry {
    public static final EntityType<AndroidEntity> ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "android"),
            EntityType.Builder.create(AndroidEntity::new, SpawnGroup.MISC).build("android")
    );
    public static final EntityType<AdvancedAndroidEntity> ADVANCED_ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "advanced_android"),
            EntityType.Builder.create(AdvancedAndroidEntity::new, SpawnGroup.MISC).build("advanced_android")
    );
    public static final EntityType<CommandAndroidEntity> COMMAND_ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "command_android"),
            EntityType.Builder.create(CommandAndroidEntity::new, SpawnGroup.MISC).build("command_android")
    );
    public static final EntityType<AndroidFrame> ANDROID_FRAME_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "unfinished_android"),
            EntityType.Builder.create(AndroidFrame::new, SpawnGroup.MISC).build("android_frame")
    );

    public static final EntityType<RogueDroidEntity> ROGUE_ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "rogue_android"),
            EntityType.Builder.create(RogueDroidEntity::new, SpawnGroup.MONSTER).build("rogue_android")
    );

    public static void register() {
        registerAttributes();
        registerSpawns();
        CCAndroids.LOGGER.info("Registered Entities");
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ANDROID_ENTITY, AndroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(ADVANCED_ANDROID_ENTITY, AdvancedAndroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(COMMAND_ANDROID_ENTITY, CommandAndroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(ROGUE_ANDROID_ENTITY, RogueDroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(ANDROID_FRAME_ENTITY, MobEntity.createMobAttributes());
    }

    private static void registerSpawns() {
        BiomeModifications.addSpawn(context -> !context.hasTag(BiomeTags.IS_OCEAN), SpawnGroup.MONSTER, ROGUE_ANDROID_ENTITY, 1, 1,1);
    }
}
