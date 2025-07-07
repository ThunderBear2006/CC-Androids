package com.thunderbear06.computer.api;

import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.api.lua.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AndroidAPI implements ILuaAPI {
    private final AndroidBrain brain;

    public AndroidAPI(AndroidBrain android) {
        this.brain = android;
    }

    @Override
    public String[] getNames() {
        return new String[]{"android"};
    }

    @Override
    public @Nullable String getModuleName() {
        return "android";
    }

    private boolean missingFuel() {
        return !this.brain.getAndroid().hasFuel();
    }

    private BlockPos getPosFromArgs(IArguments args) throws LuaException {
        int x,y,z;

        Object o = args.get(0);

        if (o instanceof Map<?,?> pos) {
            if (!pos.containsKey("x") || !pos.containsKey("y") || !pos.containsKey("z"))
                throw new LuaException("x,y,z keys expected");

            x = ((Double) pos.get("x")).intValue();
            y = ((Double) pos.get("y")).intValue();
            z = ((Double) pos.get("z")).intValue();

            return new BlockPos(x,y,z);
        }

        x = args.getInt(0);
        y = args.getInt(1);
        z = args.getInt(2);

        return new BlockPos(x,y,z);
    }

    /*
    * Information
    */

    @LuaFunction
    public final MethodResult currentTask() {
        return MethodResult.of(this.brain.getAndroid().getTaskManager().getCurrentTaskName());
    }

    @LuaFunction
    public final MethodResult getSelf() throws LuaException {
        return MethodResult.of(this.brain.getModules().sensorModule.collectEntityInfo(this.brain.getAndroid()));
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult attack(String entityUUID) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null)
            return MethodResult.of(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("attacking");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult goTo(String entityUUID) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        return this.brain.getModules().navigationModule.MoveToEntity(entityUUID);
    }

    @LuaFunction
    public final MethodResult moveTo(IArguments args) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        return this.brain.getModules().navigationModule.MoveToBlock(getPosFromArgs(args));
    }

    @LuaFunction
    public final MethodResult breakBlock(IArguments args) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        BlockPos pos = getPosFromArgs(args);

        if (!pos.isWithinDistance(this.brain.getAndroid().getBlockPos(), 100))
            return MethodResult.of(true, "Block position must be within a 100 block radius");

        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return MethodResult.of(true, "Block position must be in world build limit");

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("breakingBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useBlock(IArguments args) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        BlockPos pos = getPosFromArgs(args);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return MethodResult.of(true, "Block position must be in world build limit");

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("usingBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useEntity(String entityUUID) throws LuaException {
        if (missingFuel())
            throw new LuaException("Android requires fuel.");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return MethodResult.of(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("usingEntity");
        return MethodResult.of();
    }

    /* Disabled for now as they really don't work as expected
    @LuaFunction
    public final MethodResult startUsingItem(String handName) {
        if (handName.equals("right") || handName.equals("main"))
            this.brain.getAndroid().setCurrentHand(Hand.MAIN_HAND);
        else if (handName.equals("left") || handName.equals("off"))
            this.brain.getAndroid().setCurrentHand(Hand.OFF_HAND);
        else
            return MethodResult.of(true, "Invalid hand name");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult stopUsingItem() {
        this.brain.getAndroid().clearActiveItem();
        return MethodResult.of();
    }
    */

    /*
    * Inventory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult pickup(IArguments args) throws LuaException {
        String type = args.optString(0).orElse(null);

        ItemEntity itemEntity = brain.getModules().sensorModule.getGroundItem(type);

        if (itemEntity == null)
            return MethodResult.of(true, "Could not find item");

        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of(true, "Cannot pickup item without an empty hand");

        return this.brain.getAndroid().pickupGroundItem(itemEntity);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropItem() {
        return this.brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult storeItem(int index) {
        ItemStack itemStack = this.brain.getAndroid().getMainHandStack();

        if (itemStack.isEmpty())
            return MethodResult.of(true, "No item in hand to stash");

        MethodResult result = this.brain.getAndroid().canStash(itemStack, index);

        if (result != null)
            return result;

        itemStack = this.brain.getAndroid().stashStack(itemStack, index);
        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, itemStack);

        return MethodResult.of(false, "Stashed held item at index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult equipSlot(int index) {
        int size = brain.getAndroid().inventory.size()-1;

        if (index < 0 || index > size)
            return MethodResult.of(true, String.format("Index must be between 0 and %d", size));

        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot equip item while holding an item");

        ItemStack storedItemstack = this.brain.getAndroid().getStashItem(index, true);

        if (storedItemstack.isEmpty())
            return MethodResult.of("Index of stash is empty");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, storedItemstack);
        return MethodResult.of(false, "Equipped stack from index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult swapHands() {
        this.brain.getAndroid().swapOffHandStack();
        return MethodResult.of();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getHandInfo(String handName) {
        ItemStack stack;

        if (handName.equals("right") || handName.equals("main")) {
            stack = brain.getAndroid().getMainHandStack();
        }
        else if (handName.equals("left") || handName.equals("off"))
            stack = brain.getAndroid().getOffHandStack();
        else
            return MethodResult.of(true, "Invalid hand name");
        return MethodResult.of(stack.getName().getString(), stack.getCount());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getSlotInfo(int index) {
        int size = brain.getAndroid().inventory.size()-1;

        if (index < 0 || index > size)
            return MethodResult.of(true, String.format("Index must be between 0 and %d", size));

        ItemStack storedStack = this.brain.getAndroid().getStashItem(index, false);

        if (storedStack == null || storedStack.isEmpty())
            return MethodResult.of("empty");
        return MethodResult.of(false, storedStack.getItem().getName().getString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult refuel(IArguments args) throws LuaException {
        Optional<Integer> amt = args.optInt(0);

        ItemStack heldStack = this.brain.getAndroid().getMainHandStack();

        if (heldStack.isEmpty())
            return MethodResult.of(true, "Hand it empty");

        if (!this.brain.getAndroid().addFuel(amt.orElse(heldStack.getCount()), heldStack))
            return MethodResult.of(true, "Held item stack cannot be used for fuel");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, heldStack);

        return MethodResult.of(false, "Fuel level increased to "+ brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult fuelLevel() {
        return MethodResult.of(this.brain.getAndroid().getFuel());
    }

    /*
    * Sensory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestPlayer() throws LuaException {
        return MethodResult.of(this.brain.getModules().sensorModule.getClosestPlayer());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getNearbyMobs(IArguments args) throws LuaException {
        Optional<String> type = args.optString(0);

        return MethodResult.of(brain.getModules().sensorModule.getMobs(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestMob(IArguments args) throws LuaException {
        Optional<String> type = args.optString(0);

        return MethodResult.of(brain.getModules().sensorModule.getClosestMobOfType(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBlocksOfType(String type) {
        BlockPos pos = this.brain.getAndroid().getBlockPos();
        ServerWorld world = (ServerWorld) this.brain.getAndroid().getWorld();

        return MethodResult.of(brain.getModules().sensorModule.getBlocksOfType(pos, this.brain.getAndroid().getEyePos(), world, type));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getContainerInfo(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);

        return MethodResult.of(brain.getModules().sensorModule.GetContainerInfo(pos));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult storeHeldItemInContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);

        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);

        brain.getModules().interactionModule.StoreHeldItemInContainer(pos, slot);

        return MethodResult.of();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult grabItemFromContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);

        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);

        brain.getModules().interactionModule.GrabItemFromContainer(pos, slot);

        return MethodResult.of();
    }

    /*
    * Misc
    */

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        this.brain.getAndroid().sendChatMessage(what);
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult changeFace(String faceName) {
        this.brain.getAndroid().setFace(faceName);
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult cancelTask() {
        this.brain.getAndroid().getTaskManager().clearCurrentTask();
        return MethodResult.of();
    }
}
