package com.thunderbear06.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class AndroidInventory extends SimpleInventory {
    public AndroidInventory(int size) {
        super(size);
    }

    public NbtCompound toNbtCompound() {
        NbtCompound compound = new NbtCompound();

        for (int i = 0; i < this.size(); i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty())
                continue;

            compound.put(Integer.toString(i), stack.writeNbt(new NbtCompound()));
        }

        return compound;
    }

    public void fromNbtCompound(NbtCompound compound) {
        for (int i = 0; i < this.size(); i++) {
            String key = Integer.toString(i);
            if (compound.contains(key)) {
                setStack(i, ItemStack.fromNbt(compound.getCompound(key)));
            }
        }
    }
}
