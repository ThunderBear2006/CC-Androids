package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class CommandAndroidEntity extends AdvancedAndroidEntity{
    public CommandAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.COMMAND);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, CCAndroids.CONFIG.ComAndroidMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, CCAndroids.CONFIG.ComAndroidDamage)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, CCAndroids.CONFIG.ComAndroidSpeed)
                .add(EntityAttributes.GENERIC_ARMOR, CCAndroids.CONFIG.ComAndroidArmor);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isSourceCreativePlayer() && super.damage(source, amount);
    }

    @Override
    public boolean hasFuel() {
        return true;
    }
}
