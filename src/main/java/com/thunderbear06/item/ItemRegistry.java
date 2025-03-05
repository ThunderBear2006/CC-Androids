package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemRegistry {

    public static final Item COMPONENTS = registerItem("components", new Item(new FabricItemSettings()));
    public static final Item ANDROID_CPU = registerItem("android_cpu", new Item(new FabricItemSettings()));
    public static final Item REDSTONE_REACTOR = registerItem("redstone_reactor", new Item(new FabricItemSettings()));
    public static final Item ANDROID_FRAME = registerItem("android_frame", new AndroidFrameItem(new FabricItemSettings()));
    public static final Item WRENCH = registerItem("wrench", new WrenchItem(new FabricItemSettings().maxDamage(100)));

    public static final Item ANDROID_SPAWN_EGG = registerEgg(EntityRegistry.ANDROID_ENTITY, 0xb2b2b2,0x8a8c8b, "android_spawn");
    public static final Item ANDROID_ADVANCED_SPAWN_EGG = registerEgg(EntityRegistry.ADVANCED_ANDROID_ENTITY, 0xb2b2b2,0xa5a333, "android_advanced_spawn");
    public static final Item ANDROID_COMMAND_SPAWN_EGG = registerEgg(EntityRegistry.COMMAND_ANDROID_ENTITY, 0xfc9e46,0x9b5c22, "android_command_spawn");
    public static final Item ANDROID_ROGUE_SPAWN_EGG = registerEgg(EntityRegistry.ROGUE_ANDROID_ENTITY, 0xf41818,0x9b2222, "android_rogue_spawn");

    public static final ItemGroup ANDROIDS_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.cc-androids.android_item_group"))
            .icon(() -> new ItemStack(WRENCH))
            .entries((displayContext, entries) -> getGroupEntries(entries))
            .build();

    private static <I extends Item> Item registerItem(String name, I item) {
        return Registry.register(Registries.ITEM, new Identifier(CCAndroids.MOD_ID, name), item);
    }

    private static Item registerEgg(EntityType<? extends MobEntity> entityType, int color1, int color2, String path) {
        return registerItem(path, new SpawnEggItem(entityType, color1, color2, new FabricItemSettings()));
    }

    private static void getGroupEntries(ItemGroup.Entries entries) {
        entries.add(WRENCH);
        entries.add(COMPONENTS);
        entries.add(ANDROID_CPU);
        entries.add(REDSTONE_REACTOR);
        entries.add(ANDROID_FRAME);
        entries.add(ANDROID_SPAWN_EGG);
        entries.add(ANDROID_ADVANCED_SPAWN_EGG);
        entries.add(ANDROID_COMMAND_SPAWN_EGG);
        entries.add(ANDROID_ROGUE_SPAWN_EGG);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(CCAndroids.MOD_ID, "androids_item_group"), ANDROIDS_ITEM_GROUP);
        CCAndroids.LOGGER.info("Registered Items");
    }
}
