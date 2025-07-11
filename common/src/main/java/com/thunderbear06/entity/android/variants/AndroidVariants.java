package com.thunderbear06.entity.android.variants;

import com.thunderbear06.CCAndroids;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AndroidVariants {
    private static final HashMap<Byte, AndroidVariant> VARIANTS = new HashMap<>();

    public static void Initialize() {
        VARIANTS.put((byte) 1, new AndroidVariant(
            "android_kaylon.png", SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundEvents.BLOCK_ROOTED_DIRT_BREAK
        ));
        VARIANTS.put((byte) 2, new AndroidVariant(
            "android_pinky.png", SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundEvents.BLOCK_ROOTED_DIRT_BREAK
        ));
    }

    public static AndroidVariant getVariant(byte b) {
        return VARIANTS.get(b);
    }

    public static class AndroidVariant {
        public final Identifier texture;
        public final Identifier emissive_texture;
        public final SoundEvent ambient_sound_event;
        public final SoundEvent hurt_sound_event;
        public final SoundEvent death_sound_event;

        private AndroidVariant(String texturePath, SoundEvent ambientSoundEvent, SoundEvent hurtSoundEvent, SoundEvent deathSoundEvent) {
            this.texture = new Identifier(CCAndroids.MOD_ID,"textures/entity/variant/"+texturePath);
            this.emissive_texture = new Identifier(CCAndroids.MOD_ID,"textures/entity/emissive/variant/"+texturePath);
            this.ambient_sound_event = ambientSoundEvent;
            this.hurt_sound_event = hurtSoundEvent;
            this.death_sound_event = deathSoundEvent;
        }
    }
}
