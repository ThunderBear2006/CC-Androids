package com.thunderbear06.entity.android.frame;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class AndroidFrame extends MobEntity {
    private static final TrackedData<Byte> BUILD_PROGRESS = DataTracker.registerData(AndroidFrame.class, TrackedDataHandlerRegistry.BYTE);

    private static final TrackedData<Byte> COMPONENTS_NEEDED = DataTracker.registerData(AndroidFrame.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> INGOTS_NEEDED = DataTracker.registerData(AndroidFrame.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> HAS_CORE = DataTracker.registerData(AndroidFrame.class, TrackedDataHandlerRegistry.BOOLEAN);

    private boolean isAdvanced = false;
    private long lastHitTime = 0;

    public AndroidFrame(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BUILD_PROGRESS, (byte) 0);

        this.dataTracker.startTracking(COMPONENTS_NEEDED, CCAndroids.CONFIG.CompsForConstruction);
        this.dataTracker.startTracking(INGOTS_NEEDED, CCAndroids.CONFIG.IngotsForConstruction);
        this.dataTracker.startTracking(HAS_CORE, false);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

        World world = player.getWorld();

        if (handStack.isOf(ItemRegistry.COMPONENTS.get())) {
            if (addComponents(world)){
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if (handStack.isOf(Items.IRON_INGOT) || handStack.isOf(Items.GOLD_INGOT)) {
            if (addPlates(world, handStack.isOf(Items.GOLD_INGOT))) {
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if (handStack.isOf(ItemRegistry.REDSTONE_REACTOR.get())) {
            if (insertCore(world)) {
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if ((handStack.isOf(ItemRegistry.ANDROID_CPU.get()) || handStack.isOf(Items.COMMAND_BLOCK)) && this.isReadyForCPU()) {
            insertCPU(handStack);
            onSuccess(handStack, player, hand);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public void incrementProgress(int inc) {
        setBuildProgress(this.dataTracker.get(BUILD_PROGRESS) + inc);
    }

    private void setBuildProgress(int progress) {
        this.dataTracker.set(BUILD_PROGRESS, (byte) progress);
    }

    private void onSuccess(ItemStack stack, PlayerEntity player, Hand hand) {
        stack.decrement(1);
        player.setStackInHand(hand, stack);
    }

    private boolean addComponents(World world) {
        byte comps = this.getComponentsNeeded();

        if (comps <= 0)
            return false;

        float pitch = (getRandom().nextBetween(10,12) * 0.1f);

        this.dataTracker.set(COMPONENTS_NEEDED, --comps);

        if (comps <= 0)
            world.playSoundFromEntity(null, this, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.NEUTRAL, 1.0f, pitch);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, pitch);

        return true;
    }

    private boolean addPlates(World world, boolean isGold) {
        if (getComponentsNeeded() > 0)
            return false;

        if (getIngotsNeeded() <= 0)
            return false;

        if (isGold && !this.isAdvanced) {
            if (getIngotsNeeded() < CCAndroids.CONFIG.IngotsForConstruction)
                return false;
            this.isAdvanced = true;
        }

        if (!isGold && this.isAdvanced)
            return false;

        float pitch = (getRandom().nextBetween(10,12) * 0.1f);

        byte ingots = this.dataTracker.get(INGOTS_NEEDED);

        this.dataTracker.set(INGOTS_NEEDED, --ingots);

        if (ingots == 0)
            world.playSoundFromEntity(null, this, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.NEUTRAL, 1.0f, pitch);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, pitch);

        return true;
    }

    private boolean insertCore(World world) {
        if (hasCore())
            return false;
        this.dataTracker.set(HAS_CORE, true);

        world.playSoundFromEntity(null, this, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean isReadyForCPU() {
        return getComponentsNeeded() == 0 && hasCore() && getIngotsNeeded() == 0;
    }

    private void insertCPU(ItemStack cpu) {
        ComputerFamily family;
        int computerID = -1;

        if (cpu.isOf(Items.COMMAND_BLOCK))
            family = ComputerFamily.COMMAND;
        else
            family = this.isAdvanced ? ComputerFamily.ADVANCED : ComputerFamily.NORMAL;

        if (cpu.hasNbt() && cpu.getNbt().contains("ComputerID"))
            computerID = cpu.getNbt().getInt("ComputerID");

        finish(family, computerID);
    }

    private void finish(ComputerFamily family, int computerID) {
        BaseAndroidEntity android;

        switch (family) {
            case NORMAL -> android = EntityRegistry.ANDROID_ENTITY.get().create(getWorld());
            case ADVANCED -> android = EntityRegistry.ADVANCED_ANDROID_ENTITY.get().create(getWorld());
            case COMMAND -> android = EntityRegistry.COMMAND_ANDROID_ENTITY.get().create(getWorld());
            default -> throw new IllegalArgumentException("Unknown ComputerFamily " + family);
        }

        assert android != null;

        android.copyPositionAndRotation(this);
        android.getComputer().setComputerID(computerID);

        this.discard();
        this.getWorld().spawnEntity(android);

        android.getWorld().playSoundFromEntity(null, android, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    public byte getComponentsNeeded() {
        return this.dataTracker.get(COMPONENTS_NEEDED);
    }

    public byte getIngotsNeeded() {
        return this.dataTracker.get(INGOTS_NEEDED);
    }

    public boolean hasCore() {
        return this.dataTracker.get(HAS_CORE);
    }

    @Override
    protected void pushAway(Entity entity) {}

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    public void takeKnockback(double strength, double x, double z) {}

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient() || this.isRemoved())
            return false;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return false;
        }
        if (this.isInvulnerableTo(source))
            return false;
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            this.onBreak();
            this.kill();
            return false;
        }
        if (!(source.getAttacker() instanceof PlayerEntity))
            return false;
        if (!((PlayerEntity)source.getAttacker()).getAbilities().allowModifyWorld)
            return false;

        if (source.isSourceCreativePlayer()) {
            this.playHurtSound(source);
            this.kill();
        } else {
            long l = this.getWorld().getTime();

            if (l - this.lastHitTime > 5L) {
                this.getWorld().sendEntityStatus(this, (byte)32);
                this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
                this.lastHitTime = l;
            } else {
                this.onBreak();
            }
        }

        return true;
    }

    public void onBreak() {
        this.dropInventory();
        kill();
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.emitGameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_ANVIL_FALL;
    }

    @Override
    protected void dropInventory() {
        byte components_dropped = (byte) (CCAndroids.CONFIG.CompsForConstruction - getComponentsNeeded());

        for (int i = 0; i < components_dropped; i++) {
            this.dropStack(new ItemStack(ItemRegistry.COMPONENTS.get()));
        }

        int ingots_dropped = CCAndroids.CONFIG.IngotsForConstruction - getIngotsNeeded();

        for (int j = 0; j < ingots_dropped; j++) {
            this.dropStack(new ItemStack(this.isAdvanced ? Items.GOLD_INGOT : Items.IRON_INGOT));
        }

        if (hasCore())
            this.dropStack(new ItemStack(ItemRegistry.REDSTONE_REACTOR.get()));

        this.dropStack(new ItemStack(ItemRegistry.ANDROID_FRAME.get()));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putByte("ComponentsNeeded", getComponentsNeeded());
        nbt.putByte("IngotsNeeded", getIngotsNeeded());
        nbt.putBoolean("IsAdvanced", this.isAdvanced);
        nbt.putBoolean("HasCore", hasCore());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ComponentsNeeded")) {
            this.dataTracker.set(COMPONENTS_NEEDED, nbt.getByte("ComponentsNeeded"));
            this.dataTracker.set(INGOTS_NEEDED, nbt.getByte("IngotsNeeded"));
            this.dataTracker.set(HAS_CORE, nbt.getBoolean("HasCore"));
            this.isAdvanced = nbt.getBoolean("IsAdvanced");
        }
        super.readCustomDataFromNbt(nbt);
    }
}
