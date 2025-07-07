package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.AndroidLookAtEntityGoal;
import com.thunderbear06.ai.task.TaskManager;
import com.thunderbear06.ai.task.tasks.*;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.sounds.SoundRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class AndroidEntity extends BaseAndroidEntity {
    protected final TaskManager taskManager;
    private static final TrackedData<Boolean> IS_LOCKED = DataTracker.registerData(AndroidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> VARIANT = DataTracker.registerData(AndroidEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> FACE = DataTracker.registerData(AndroidEntity.class, TrackedDataHandlerRegistry.BYTE);

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.brain = new AndroidBrain(this);
        this.taskManager = new TaskManager();
        this.computerContainer.setFamily(ComputerFamily.NORMAL);

        addAndroidTasks();
        initAndroidGoals();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_LOCKED, false);
        this.dataTracker.startTracking(VARIANT, (byte) 0);
        this.dataTracker.startTracking(FACE, (byte) 0);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, CCAndroids.CONFIG.AndroidMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, CCAndroids.CONFIG.AndroidDamage)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, CCAndroids.CONFIG.AndroidSpeed)
                .add(EntityAttributes.GENERIC_ARMOR, CCAndroids.CONFIG.AndroidArmor);
    }

    protected void addAndroidTasks() {
        this.taskManager.addTask(new AttackEntityTask(this, 0.5f));
        this.taskManager.addTask(new BreakBlockTask(this, 0.5f));
        this.taskManager.addTask(new InteractBlockTask(this, 0.5f));
        this.taskManager.addTask(new InteractEntityTask(this, 0.5f));
        this.taskManager.addTask(new MoveToBlockTask(this, 0.5f));
        this.taskManager.addTask(new MoveToEntityTask(this, 0.5f));
    }

    protected void initAndroidGoals() {
        this.goalSelector.add(0, new AndroidLookAtEntityGoal(this, PlayerEntity.class, 10));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (hasFuel())
            this.taskManager.tick();
        else if (!this.getNavigation().isIdle())
            this.getNavigation().stop();
    }

    @Override
    protected boolean isIdle() {
        return !this.taskManager.hasTask();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (isLocked() && !this.brain.isOwningPlayer(player)) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            player.sendMessage(Text.translatable("entity.cc-androids.android.locked"), true);
            return ActionResult.FAIL;
        }

        if (player.isSneaking()) {
            player.setStackInHand(Hand.MAIN_HAND, swapHandStack(player.getStackInHand(hand)));
            return ActionResult.SUCCESS;
        }

        ItemStack playerHandStack = player.getStackInHand(hand);

        ActionResult itemUseResult = handleItemUse(playerHandStack);

        if (itemUseResult != null)
            return itemUseResult;

        if (!getWorld().isClient()) {
            if (playerHandStack.isOf(Items.TRIPWIRE_HOOK) && this.brain.isOwningPlayer(player)) {
                setLocked(!isLocked());
                return ActionResult.SUCCESS;
            }

            if (this.brain.getOwningPlayerProfile() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            this.getComputer().openComputer((ServerPlayerEntity) player);
        }

        return ActionResult.CONSUME;
    }

    private ActionResult handleItemUse(ItemStack stack) {
        if (stack.isOf(ItemRegistry.WRENCH)) {
            return ActionResult.PASS;
        }

        if (stack.isOf(ItemRegistry.COMPONENTS)) {
            repair(stack);
            return ActionResult.SUCCESS;
        }

        if (stack.isOf(Items.GRAY_DYE)) {
            setVariant((byte) 1);
            return ActionResult.SUCCESS;
        }

        if (stack.isOf(Items.PINK_DYE)) {
            setVariant((byte) 2);
            return ActionResult.SUCCESS;
        }

        return null;
    }

    public boolean isLocked() {
        return this.dataTracker.get(IS_LOCKED);
    }

    public void setLocked(boolean locked) {
        this.dataTracker.set(IS_LOCKED, locked);
    }

    public byte getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public boolean hasVariant() {
        return getVariant() > 0;
    }

    public void setFace(String faceName) {
        byte face = switch (faceName) {
            case "angry" -> 1;
            case "annoyed" -> 2;
            case "happy" -> 3;
            case "sad" -> 4;
            case "woozy" -> 5;
            default -> 0;
        };

        this.dataTracker.set(FACE, face);
    }

    public byte getFace() {
        return this.dataTracker.get(FACE);
    }

    public void deconstruct() {
        super.dropInventory();
        this.dropComponents(true);
        this.dropIngots(true);

        AndroidFrame frame = this.convertTo(EntityRegistry.ANDROID_FRAME_ENTITY, false);
        frame.copyPositionAndRotation(this);
        this.getWorld().playSound(null, getBlockPos(), SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    protected ItemStack swapHandStack(ItemStack stack) {
        ItemStack heldStack = this.getMainHandStack();

        if (stack.isIn(ItemTags.FLOWERS))
            spawnHearts();

        this.setStackInHand(Hand.MAIN_HAND, stack);
        return heldStack;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    public boolean repair(ItemStack stack) {
        if (this.getHealth() < this.getMaxHealth()) {
            this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            this.heal(5);
            stack.decrement(1);
            return true;
        }
        return false;
    }

    private void spawnHearts() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
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

    @Override
    protected void dropInventory() {
        super.dropInventory();

        dropIngots(false);
        dropComponents(false);
    }

    protected void dropIngots(boolean full) {
        this.dropStack(Items.IRON_INGOT.getDefaultStack().copyWithCount((int) (CCAndroids.CONFIG.IngotsForConstruction * (full ? 1.0 : CCAndroids.CONFIG.IngotsDroppedOnDeathPercentage))));
    }

    protected void dropComponents(boolean full) {
        this.dropStack(ItemRegistry.COMPONENTS.getDefaultStack().copyWithCount((int) (CCAndroids.CONFIG.CompsForConstruction * (full ? 1.0 : CCAndroids.CONFIG.CompsDroppedOnDeathPercentage))));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("Variant", this.getVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(VARIANT, nbt.getByte("Variant"));
    }
}
