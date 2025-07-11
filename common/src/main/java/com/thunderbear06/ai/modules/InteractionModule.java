package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InteractionModule extends AbstractAndroidModule {
    public InteractionModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public void interactWithBlock(Hand hand, BlockPos pos) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        this.android.swingHand(hand);

        player.interactionManager.interactBlock(player, this.android.getWorld(), player.getStackInHand(hand), hand, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, true));
    }

    public void interactWithEntity(Hand hand, LivingEntity entity) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        ItemStack handStack = player.getStackInHand(hand);

        this.android.swingHand(hand);

        if (entity instanceof AndroidEntity droid && handStack.isOf(ItemRegistry.COMPONENTS.get())) {
            if (droid.repair(handStack))
                return;
        }

        if (entity instanceof MobEntity mob) {
            if (handStack.isOf(Items.LEAD) && mob.getHoldingEntity() == null) {
                mob.attachLeash(this.android, true);
                handStack.decrement(1);
                this.android.setStackInHand(hand, handStack);
                return;
            } else if (handStack.isEmpty() && mob.getHoldingEntity() != null && mob.getHoldingEntity().equals(this.android)) {
                mob.detachLeash(true, true);
                return;
            }
        }

        entity.interact(player, hand);
    }

    public void tickDoorInteraction()
    {
        MobNavigation nav = (MobNavigation) android.getNavigation();

        if (nav.isIdle() || nav.getCurrentPath() == null)
            return;
        PathNode node = nav.getCurrentPath().getCurrentNode();
        PathNode lastNode = nav.getCurrentPath().getLastNode();

        toggleDoor(node.getBlockPos(), true);
        if (lastNode != null && lastNode.previous != null)
            toggleDoor(lastNode.previous.getBlockPos(), false);
    }

    private void toggleDoor(BlockPos pos, boolean open) {
        BlockState state = android.getWorld().getBlockState(pos);

        if (state.isIn(BlockTags.WOODEN_DOORS)) {
            DoorBlock door = (DoorBlock) state.getBlock();
            door.setOpen(android, android.getWorld(), state, pos, open);
        }
    }

    public void StoreHeldItemInContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.isWithinDistance(android.getPos(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayerEntity androidPlr = AndroidPlayer.get(brain).player();

        ItemStack heldStack = androidPlr.getMainHandStack();

        BlockEntity blockEntity = android.getWorld().getBlockEntity(pos);

        if (!(blockEntity instanceof Inventory inv))
            throw new LuaException("Targeted block does not have an inventory");
        if (blockEntity instanceof LockableContainerBlockEntity locked && !locked.checkUnlocked(androidPlr))
            throw new LuaException("Targeted container is locked!");
        if (!inv.isValid(slot,heldStack))
            throw new LuaException("Held item can not be placed in that slot");

        ItemStack invStack = inv.getStack(slot);

        inv.onOpen(androidPlr);

        android.swingHand(Hand.MAIN_HAND);

        if (invStack.isEmpty())
        {
            inv.setStack(slot, heldStack.copy());

            android.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
        else if (ItemStack.canCombine(invStack, heldStack))
        {
            int space = invStack.getMaxCount() - invStack.getCount();
            int transfer = Math.min(heldStack.getCount(), space);

            invStack.increment(transfer);

            heldStack.decrement(transfer);
        }
        else
        {
            throw new LuaException("There is already a item in that slot");
        }

        inv.onClose(androidPlr);
    }

    public void GrabItemFromContainer(BlockPos pos, int slot) throws LuaException
    {
        if (!pos.isWithinDistance(android.getPos(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayerEntity androidPlr = AndroidPlayer.get(brain).player();

        ItemStack heldStack = android.getMainHandStack();

        BlockEntity blockEntity = android.getWorld().getBlockEntity(pos);

        if (!(blockEntity instanceof Inventory inv))
            throw new LuaException("Targeted block does not have an inventory");
        if (blockEntity instanceof LockableContainerBlockEntity locked && !locked.checkUnlocked(androidPlr))
            throw new LuaException("Targeted container is locked!");

        ItemStack invStack = inv.getStack(slot);

        if (invStack.isEmpty())
            throw new LuaException("Slot is empty");

        inv.onOpen(androidPlr);

        android.swingHand(Hand.MAIN_HAND);

        if (heldStack.isEmpty())
        {
            android.setStackInHand(Hand.MAIN_HAND, invStack.copy());

            inv.setStack(slot, ItemStack.EMPTY);
        }
        else if (ItemStack.canCombine(invStack, heldStack))
        {
            int space = heldStack.getMaxCount() - heldStack.getCount();
            int transfer = Math.min(invStack.getCount(), space);

            heldStack.increment(transfer);

            invStack.decrement(transfer);
        }
        else
        {
            throw new LuaException("Slot is blocked by item");
        }

        inv.onClose(androidPlr);
    }
}
