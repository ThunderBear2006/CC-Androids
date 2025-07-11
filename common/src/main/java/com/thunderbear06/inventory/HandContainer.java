package com.thunderbear06.inventory;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class HandContainer implements Inventory {
    private final BaseAndroidEntity android;

    public HandContainer(BaseAndroidEntity android) {
        this.android = android;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return android.getMainHandStack().isEmpty() && android.getOffHandStack().isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return switch (slot) {
            case 0 -> android.getMainHandStack();
            case 1 -> android.getOffHandStack();
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        };
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (amount <= 0)
            return ItemStack.EMPTY;
        if (amount >= getStack(slot).getCount())
            return removeStack(slot);

        ItemStack result = getStack(slot).copyWithCount(amount);
        getStack(slot).decrement(amount);

        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removedStack;

        switch (slot) {
            case 0 -> {
                removedStack = android.getMainHandStack();
                android.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
            case 1 -> {
                removedStack = android.getOffHandStack();
                android.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            }
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        }

        return removedStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        switch (slot) {
            case 0 -> android.setStackInHand(Hand.MAIN_HAND, stack);
            case 1 -> android.setStackInHand(Hand.OFF_HAND, stack);
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        }
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        android.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        android.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }
}
