package com.thunderbear06.item;

import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Consumer;

public class AndroidFrameItem extends Item {
    public AndroidFrameItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        }

        World world = context.getWorld();
        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        ItemStack itemStack = context.getStack();
        Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
        Box box = EntityRegistry.ANDROID_FRAME_ENTITY.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());

        if (world.isSpaceEmpty(null, box) && world.getOtherEntities(null, box).isEmpty()) {
            if (world instanceof ServerWorld serverWorld) {
                Consumer<AndroidFrame> consumer = EntityType.copier(serverWorld, itemStack, context.getPlayer());
                AndroidFrame frame = EntityRegistry.ANDROID_FRAME_ENTITY.create(serverWorld, itemStack.getNbt(), consumer, blockPos, SpawnReason.SPAWN_EGG, true, true);

                if (frame == null) {
                    return ActionResult.FAIL;
                }

                float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                frame.refreshPositionAndAngles(frame.getX(), frame.getY(), frame.getZ(), f, 0.0F);
                serverWorld.spawnEntityAndPassengers(frame);
                world.playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.75F, 0.8F);
                frame.emitGameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }

        return ActionResult.FAIL;
    }
}
