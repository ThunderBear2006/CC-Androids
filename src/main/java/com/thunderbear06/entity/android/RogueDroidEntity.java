package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.sounds.SoundRegistry;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class RogueDroidEntity extends HostileEntity {
    public RogueDroidEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, CCAndroids.CONFIG.RogueMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, CCAndroids.CONFIG.RogueDamage)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, CCAndroids.CONFIG.RogueSpeed)
                .add(EntityAttributes.GENERIC_ARMOR, CCAndroids.CONFIG.RogueArmor);
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if (spawnReason == SpawnReason.NATURAL) {
            if (!CCAndroids.CONFIG.RoguesSpawnNaturally)
                return false;
            if (world.isSkyVisible(this.getBlockPos()))
                return false;
            if (world.getLightLevel(this.getBlockPos()) > 3)
                return false;
        }

        return super.canSpawn(world, spawnReason);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason.equals(SpawnReason.NATURAL) && CCAndroids.CONFIG.RoguesSpawnWithTools) {
            int rng = this.getRandom().nextInt(10);

            ItemStack handStack = switch (rng) {
                case 5 -> Items.WOODEN_SHOVEL.getDefaultStack();
                case 6 -> Items.WOODEN_HOE.getDefaultStack();
                case 7 -> Items.WOODEN_PICKAXE.getDefaultStack();
                case 8 -> Items.WOODEN_SWORD.getDefaultStack();
                case 9 -> Items.STICK.getDefaultStack();
                case 10 -> Items.IRON_SHOVEL.getDefaultStack();
                default -> ItemStack.EMPTY;
            };

            setStackInHand(Hand.MAIN_HAND, handStack);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f, 1.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.5));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.5, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
    }

    @Override
    public int getAir() {
        return 10;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundRegistry.ANDROID_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.ANDROID_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.ANDROID_DEATH;
    }
}
