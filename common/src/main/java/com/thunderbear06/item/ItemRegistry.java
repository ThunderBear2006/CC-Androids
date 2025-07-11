package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class ItemRegistry {
    public static final DeferredRegister<ItemGroup> ITEM_GROUPS = DeferredRegister.create(CCAndroids.MOD_ID, RegistryKeys.ITEM_GROUP);
    public static final RegistrySupplier<ItemGroup> ANDROIDS_ITEM_GROUP = ITEM_GROUPS.register("androids_item_group",
            () -> CreativeTabRegistry.create(
                    Text.translatable("itemGroup.cc_androids.android_item_group"),
                    () -> new ItemStack(ItemRegistry.WRENCH.get())));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(CCAndroids.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP).maxDamage(100)));
    public static final RegistrySupplier<Item> COMPONENTS = ITEMS.register("components", () -> new Item(new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP)));
    public static final RegistrySupplier<Item> ANDROID_CPU = ITEMS.register("android_cpu", () -> new Item(new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP)));
    public static final RegistrySupplier<Item> REDSTONE_REACTOR = ITEMS.register("redstone_reactor", () -> new Item(new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP)));
    public static final RegistrySupplier<Item> ANDROID_FRAME = ITEMS.register("android_frame", () -> new AndroidFrameItem(new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP)));

// FIXME: breaks on startup on forge
//    public static final RegistrySupplier<Item> ANDROID_SPAWN_EGG = registerEgg(EntityRegistry.ANDROID_ENTITY, 0xb2b2b2,0x8a8c8b, "android_spawn");
//    public static final RegistrySupplier<Item> ANDROID_ADVANCED_SPAWN_EGG = registerEgg(EntityRegistry.ADVANCED_ANDROID_ENTITY, 0xb2b2b2,0xa5a333, "android_advanced_spawn");
//    public static final RegistrySupplier<Item> ANDROID_COMMAND_SPAWN_EGG = registerEgg(EntityRegistry.COMMAND_ANDROID_ENTITY, 0xfc9e46,0x9b5c22, "android_command_spawn");
//    public static final RegistrySupplier<Item> ANDROID_ROGUE_SPAWN_EGG = registerEgg(EntityRegistry.ROGUE_ANDROID_ENTITY, 0xf41818,0x9b2222, "android_rogue_spawn");
//
//    private static RegistrySupplier<Item> registerEgg(RegistrySupplier<? extends EntityType<? extends MobEntity>> entityType, int color1, int color2, String path) {
//        return ITEMS.register(path, () -> new SpawnEggItem(entityType.get(), color1, color2, new Item.Settings().arch$tab(ANDROIDS_ITEM_GROUP)));
//    }

    public static void register() {
        ITEMS.register();
        ITEM_GROUPS.register();
        CCAndroids.LOGGER.info("Registered Items");
    }
}
