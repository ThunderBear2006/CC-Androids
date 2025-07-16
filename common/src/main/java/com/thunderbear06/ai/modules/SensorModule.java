package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class SensorModule extends AbstractAndroidModule {

    private final double entitySearchRadius;
    private final int blockSearchRadius;

    public SensorModule(BaseAndroidEntity android, AndroidBrain brain, double searchRadius, int blockSearchRadius) {
        super(android, brain);
        this.entitySearchRadius = searchRadius;
        this.blockSearchRadius = blockSearchRadius;
    }

    public List<HashMap<String, Object>> getMobs(@Nullable String type) {
        List<HashMap<String, Object>> result = new ArrayList<>();

        this.android.getWorld().getEntitiesByClass(LivingEntity.class, this.android.getBoundingBox().expand(this.entitySearchRadius), getTypePredicate(type)).forEach(entity -> {
            try {
                result.add(collectEntityInfo(entity));
            } catch (LuaException ignored) {}
        });

        return result;
    }

    public HashMap<String, Object> getClosestMobOfType(@Nullable String type) throws LuaException {
        BlockPos pos = this.android.getBlockPos();

        Entity entity = this.android.getWorld().getClosestEntity(
                LivingEntity.class,
                TargetPredicate.DEFAULT.setPredicate(getTypePredicate(type)),
                this.android,
                pos.getX(),
                pos.getY(),
                pos.getX(),
                this.android.getBoundingBox().expand(this.entitySearchRadius)
        );

        if (entity == null || entity instanceof LivingEntity livingEntity && livingEntity.isDead())
            return new HashMap<>();

        return collectEntityInfo(entity);
    }

    public HashMap<String, Object> getClosestPlayer() throws LuaException {
        ServerPlayerEntity player = (ServerPlayerEntity) this.android.getWorld().getClosestPlayer(this.android, 100);

        if (player == null)
            return new HashMap<>();

        return collectEntityInfo(player);
    }

    public @Nullable ItemEntity getGroundItem(@Nullable String type) {
        List<ItemEntity> items = this.android.getWorld().getNonSpectatingEntities(ItemEntity.class, this.android.getBoundingBox().expand(5));

        for (ItemEntity entity : items) {
            if (type == null || Registries.ITEM.getId(entity.getStack().getItem()).toString().contains(type))
                return entity;
        }

        return null;
    }

    public List<HashMap<String, Integer>> getBlocksOfType(BlockPos origin, Vec3d eyePos, World world, String type) {

        List<HashMap<String, Integer>> blocks = new ArrayList<>();

        for (BlockPos pos : BlockPos.iterateOutwards(origin, this.blockSearchRadius, this.blockSearchRadius, this.blockSearchRadius)) {
            if (!Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString().contains(type))
                continue;

            for (Direction direction : Direction.stream().toList()) {
                if (world.getBlockState(pos.offset(direction)).isSolidBlock(world, pos.offset(direction)))
                    continue;

                RaycastContext context = new RaycastContext(eyePos, pos.offset(direction).toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, this.android);
                if (!world.raycast(context).getBlockPos().equals(pos.offset(direction)))
                    continue;

                blocks.add(new HashMap<>() {{put("x", pos.getX()); put("y", pos.getY()); put("z", pos.getZ());}} );
                break;
            }
        }

        return blocks;
    }

    public HashMap<String, Object> collectEntityInfo(Entity entity) throws LuaException {
        HashMap<String, Object> infoMap = new HashMap<>();

        infoMap.put("uuid", entity.getUuidAsString());
        infoMap.put("name", entity.getName().getString());
        infoMap.put("posX", entity.getX());
        infoMap.put("posY", entity.getY());
        infoMap.put("posZ", entity.getZ());
        if (entity instanceof LivingEntity livingEntity) {
            infoMap.put("health", livingEntity.getHealth());
        }

        return infoMap;
    }

    private Predicate<LivingEntity> getTypePredicate(@Nullable String type) {
        if (type == null) {
            return (entity -> entity != this.android && this.android.canSee(entity));
        } else {
            return (entity -> EntityType.getId(entity.getType()).toString().contains(type)
                    && entity != this.android
                    && !entity.isSpectator()
                    && entity.isAlive()
                    && this.android.canSee(entity));
        }
    }

    public HashMap<String, Object> GetContainerInfo(BlockPos pos) throws LuaException {
        if (!pos.isWithinDistance(android.getPos(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayerEntity androidPlr = AndroidPlayer.get(brain).player();

        BlockEntity blockEntity = android.getWorld().getBlockEntity(pos);

        HashMap<String, Object> infoMap = new HashMap<>();

        if (!(blockEntity instanceof Inventory inv))
            return infoMap;

        infoMap.put("slotCount", inv.size());
        infoMap.put("locked", blockEntity instanceof LockableContainerBlockEntity locked && !locked.checkUnlocked(androidPlr));

        List<List<Object>> items = new ArrayList<>();

        for (int i = 0; i < inv.size(); i++)
        {
            ItemStack stack = inv.getStack(i);

            List<Object> itemInfo = new ArrayList<>();

            itemInfo.add(stack.getName().toString());
            itemInfo.add(stack.getCount());

            items.add((itemInfo));
        }

        infoMap.put("slots", items);

        return infoMap;
    }
}
