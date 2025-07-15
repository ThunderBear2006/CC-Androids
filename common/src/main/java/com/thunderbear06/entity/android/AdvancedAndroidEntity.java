package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class AdvancedAndroidEntity extends AndroidEntity{
    public AdvancedAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.ADVANCED);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, CCAndroids.CONFIG.AdvAndroidMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, CCAndroids.CONFIG.AdvAndroidDamage)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, CCAndroids.CONFIG.AdvAndroidSpeed)
                .add(EntityAttributes.GENERIC_ARMOR, CCAndroids.CONFIG.AdvAndroidArmor);
    }

    @Override
    public double getEntitySearchRadius() {
        return super.getEntitySearchRadius() * 3;
    }

    @Override
    public int getBlockSearchRadius() {
        return super.getBlockSearchRadius() * 3;
    }

    @Override
    protected void dropIngots(boolean full) {
        this.dropStack(Items.GOLD_INGOT.getDefaultStack().copyWithCount((int) (CCAndroids.CONFIG.IngotsForConstruction * (full ? 1.0 : 0.5))));
    }
}
