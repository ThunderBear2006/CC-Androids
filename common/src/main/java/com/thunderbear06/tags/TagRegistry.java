package com.thunderbear06.tags;

import com.thunderbear06.CCAndroids;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagRegistry {
    public static final TagKey<Item> MINOR_ANDROID_FUEL = registerTag("minor_android_fuel");
    public static final TagKey<Item> MEDIUM_ANDROID_FUEL = registerTag("medium_android_fuel");
    public static final TagKey<Item> MAJOR_ANDROID_FUEL = registerTag("major_android_fuel");

    public static TagKey<Item> registerTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(CCAndroids.MOD_ID, name));
    }
}
