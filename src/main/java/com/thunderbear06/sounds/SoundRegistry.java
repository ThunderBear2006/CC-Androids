package com.thunderbear06.sounds;

import com.thunderbear06.CCAndroids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegistry {
    public static final SoundEvent ANDROID_AMBIENT = registerSound("android_ambient");
    public static final SoundEvent ANDROID_HURT = registerSound("android_hurt");
    public static final SoundEvent ANDROID_DEATH = registerSound("android_death");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(CCAndroids.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void register() {
        CCAndroids.LOGGER.info("Registered Sound Events");
    }
}
