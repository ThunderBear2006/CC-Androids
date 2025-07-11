package com.thunderbear06.sounds;

import com.thunderbear06.CCAndroids;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(CCAndroids.MOD_ID, RegistryKeys.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> ANDROID_AMBIENT = registerSound("android_ambient");
    public static final RegistrySupplier<SoundEvent> ANDROID_HURT = registerSound("android_hurt");
    public static final RegistrySupplier<SoundEvent> ANDROID_DEATH = registerSound("android_death");

    private static RegistrySupplier<SoundEvent> registerSound(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.of(Identifier.of(CCAndroids.MOD_ID, id)));
    }

    public static void register() {
        SOUND_EVENTS.register();
        CCAndroids.LOGGER.info("Registered Sound Events");
    }
}
