package com.thunderbear06.computer.api;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.util.PathReachChecker;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AndroidAPI implements ILuaAPI {
    private final NewAndroidBrain brain;

    public AndroidAPI(NewAndroidBrain android) {
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


    public static MethodResult Result(boolean failure, String reason) {
        return MethodResult.of(failure, reason);
    }

    private boolean checkFuel() {
        return this.brain.getAndroid().hasFuel();
    }

    /*
    * Information
    */

    @LuaFunction(mainThread = true)
    public final MethodResult getPosition() {
        BlockPos blockPos = this.brain.getAndroid().getBlockPos();
        Map<String, Integer> posMap = new HashMap<>();
        posMap.put("x", blockPos.getX());
        posMap.put("y", blockPos.getY());
        posMap.put("z", blockPos.getZ());

        return MethodResult.of(posMap);
    }

    @LuaFunction
    public final MethodResult getState() {
        return MethodResult.of(this.brain.getAndroid().getTaskManager().getCurrentTaskName());
    }

    @LuaFunction
    public final MethodResult getHealth() {
        return MethodResult.of(this.brain.getAndroid().getHealth());
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult attack(String entityUUID) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null)
            return Result(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("attacking");
        return Result(false, "Attacking "+target.getName().getString());
    }

    @LuaFunction
    public final MethodResult goTo(String entityUUID) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        return this.brain.getModules().navigationModule.MoveToEntity(entityUUID);
    }

    @LuaFunction
    public final MethodResult moveTo(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        return this.brain.getModules().navigationModule.MoveToBlock(new BlockPos(x,y,z));
    }

    @LuaFunction
    public final MethodResult breakBlock(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        BlockPos pos = new BlockPos(x,y,z);

        if (!pos.isWithinDistance(this.brain.getAndroid().getBlockPos(), 100))
            return Result(true, "Block position must be within a 100 block radius");

        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return Result(true, "Block position must be in world build limit");

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("breakingBlock");
        return Result(false, "Mining block at "+pos);
    }

    @LuaFunction
    public final MethodResult useBlock(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        BlockPos pos = new BlockPos(x,y,z);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return Result(true, "Block position must be in world build limit");

        BlockPos closest = PathReachChecker.getClosestPosition(this.brain.getAndroid().getBlockPos(), pos, (ServerWorld) this.brain.getAndroid().getWorld());
        if (closest != null) {
            if (!closest.isWithinDistance(pos, 2))
                return Result(true, "Could not find path to a position within 3 blocks of target");
        }

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("usingBlock");
        return Result(false, "Using block at "+pos);
    }

    @LuaFunction
    public final MethodResult useEntity(String entityUUID) {
        if (!checkFuel())
            return Result(true, "Fuel required for this action");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return Result(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("interactEntity");
        return Result(false, "Using "+target.getName().getString());
    }

    /*
    * Inventory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult pickup(String entityUUID) {
        ServerWorld world = (ServerWorld) this.brain.getAndroid().getWorld();

        ItemEntity itemEntity = (ItemEntity) world.getEntity(UUID.fromString(entityUUID));

        if (itemEntity == null)
            return Result(true, "Unknown item or invalid UUID");

        if (this.brain.getAndroid().distanceTo(itemEntity) > 2)
            return Result(true, "Item is too far to pick up");

        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return Result(true, "Cannot pickup item without an empty hand");

        return this.brain.getAndroid().pickupGroundItem(itemEntity);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropHeldItem() {
        return this.brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult stashHeldItem(int index) {
        if (index < 1)
            return Result(true, "Index must start at 1");

        index--;

        ItemStack itemStack = this.brain.getAndroid().getMainHandStack();

        if (itemStack.isEmpty())
            return Result(true, "No item in hand to stash");

        MethodResult result = this.brain.getAndroid().canStash(itemStack, index);

        if (result != null)
            return result;

        itemStack = this.brain.getAndroid().stashStack(itemStack, index);
        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, itemStack);

        return Result(false, "Stashed held item at index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult equipFromStash(int index) {
        if (index < 1)
            return Result(true, "Index must start at 1");

        index--;

        ItemStack storedItemstack = this.brain.getAndroid().getStashItem(index, true);

        if (storedItemstack == null || storedItemstack.isEmpty())
            return MethodResult.of("Index of stash is empty");
        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot equip item while holding an item");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, storedItemstack);
        return Result(false, "Equipped stack from index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItemInStash(int index) {
        if (index < 1)
            return Result(true, "Index must start at 1");

        index--;

        ItemStack storedStack = this.brain.getAndroid().getStashItem(index, false);

        if (storedStack.isEmpty())
            return MethodResult.of("empty");
        return Result(false, storedStack.getItem().getName().getString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult refuel(Optional<Integer> amt) {
        int refuelAmt = amt.orElse(Integer.MAX_VALUE);

        ItemStack heldStack = this.brain.getAndroid().getMainHandStack();

        if (heldStack.isEmpty())
            return Result(true, "Must be holding redstone to refuel");

        refuelAmt = Math.min(refuelAmt, heldStack.getCount());

        heldStack.decrement(this.brain.getAndroid().addFuel(refuelAmt));

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, heldStack);

        return Result(false, "Fuel level increased to "+ brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getFuelLevel() {
        return MethodResult.of(this.brain.getAndroid().getFuel());
    }

    /*
    * Sensory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestPlayer() {
        return MethodResult.of(this.brain.getModules().sensorModule.getClosestPlayer());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getNearbyMobs(Optional<String> type) {
        return MethodResult.of(brain.getModules().sensorModule.getMobs(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestMobOfType(Optional<String> type) {
        return MethodResult.of(brain.getModules().sensorModule.getClosestMobOfType(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getGroundItems(Optional<String> type, Optional<Integer> max) {
        return MethodResult.of(brain.getModules().sensorModule.getGroundItem(type.orElse(null), max.orElse(Integer.MAX_VALUE)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBlocksOfType(String type) {
        BlockPos pos = this.brain.getAndroid().getBlockPos();
        ServerWorld world = (ServerWorld) this.brain.getAndroid().getWorld();

        return MethodResult.of(brain.getModules().sensorModule.getBlocksOfType(pos, this.brain.getAndroid().getEyePos(), world, type));
    }

    /*
    * Misc
    */

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        this.brain.getAndroid().sendChatMessage(what);
        return MethodResult.of();
    }

    //TODO: Wish this could return NBT

    @LuaFunction(mainThread = true)
    public final MethodResult getMobInfo(String entityUUIDString) {
        ServerWorld world = (ServerWorld) brain.getAndroid().getWorld();

        LivingEntity entity = (LivingEntity) world.getEntity(UUID.fromString(entityUUIDString));

        if (entity == null)
            return MethodResult.of("Entity does not exist");

        HashMap<String, Object> infoMap = new HashMap<>();

        infoMap.put("name", Objects.requireNonNullElse(entity.getCustomName(), entity.getName()).getString());
        infoMap.put("health", entity.getHealth());
        infoMap.put("isHostile", entity instanceof HostileEntity);
        infoMap.put("posX", entity.getX());
        infoMap.put("posY", entity.getY());
        infoMap.put("posZ", entity.getZ());

        return MethodResult.of(infoMap);
    }
}
