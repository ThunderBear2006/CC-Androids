package com.thunderbear06.entity;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.*;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.SpawnSettings;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(CCAndroids.MOD_ID, RegistryKeys.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<AndroidEntity>> ANDROID_ENTITY = ENTITY_TYPES.register(
            "android",
            () -> EntityType.Builder.create(AndroidEntity::new, SpawnGroup.MISC).build("android")
    );
    public static final RegistrySupplier<EntityType<AdvancedAndroidEntity>> ADVANCED_ANDROID_ENTITY = ENTITY_TYPES.register(
            "advanced_android",
            () -> EntityType.Builder.create(AdvancedAndroidEntity::new, SpawnGroup.MISC).build("advanced_android")
    );
    public static final RegistrySupplier<EntityType<CommandAndroidEntity>> COMMAND_ANDROID_ENTITY = ENTITY_TYPES.register(
            "command_android",
            () -> EntityType.Builder.create(CommandAndroidEntity::new, SpawnGroup.MISC).build("command_android")
    );
    public static final RegistrySupplier<EntityType<AndroidFrame>> ANDROID_FRAME_ENTITY = ENTITY_TYPES.register(
            "unfinished_android",
            () -> EntityType.Builder.create(AndroidFrame::new, SpawnGroup.MISC).build("android_frame")
    );

    public static final RegistrySupplier<EntityType<RogueDroidEntity>> ROGUE_ANDROID_ENTITY = ENTITY_TYPES.register(
            "rogue_android",
            () -> EntityType.Builder.create(RogueDroidEntity::new, SpawnGroup.MONSTER).build("rogue_android")
    );

    public static void register() {
        ENTITY_TYPES.register();
        registerAttributes();
        registerSpawns();
        CCAndroids.LOGGER.info("Registered Entities");
    }

    private static void registerAttributes() {
        EntityAttributeRegistry.register(ANDROID_ENTITY, AndroidEntity::createAndroidAttributes);
        EntityAttributeRegistry.register(ADVANCED_ANDROID_ENTITY, AdvancedAndroidEntity::createAndroidAttributes);
        EntityAttributeRegistry.register(COMMAND_ANDROID_ENTITY, CommandAndroidEntity::createAndroidAttributes);
        EntityAttributeRegistry.register(ROGUE_ANDROID_ENTITY, RogueDroidEntity::createAndroidAttributes);
        EntityAttributeRegistry.register(ANDROID_FRAME_ENTITY, MobEntity::createMobAttributes);
    }

    private static void registerSpawns() {
        BiomeModifications.addProperties(context -> !context.hasTag(BiomeTags.IS_OCEAN), (context, properties) -> properties.getSpawnProperties().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ROGUE_ANDROID_ENTITY.get(), 1, 1,1)));
    }
}
