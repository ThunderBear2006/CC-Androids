package com.thunderbear06.menu;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.inventory.HandContainer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AndroidMenu extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int PLAYER_START_X = SIDEBAR_WIDTH + BORDER;
    public static final int ANDROID_START_X = SIDEBAR_WIDTH + 175;

    public AndroidMenu(int id, Predicate<PlayerEntity> canUse, ComputerFamily family, @Nullable ServerComputer computer, @Nullable ComputerContainerData containerData, PlayerInventory playerInventory, SimpleInventory inventory, Inventory container) {
        super(MenuRegistry.ANDROID.get(), id, canUse, family, computer, containerData);

        // Android Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(inventory, x + y * 3, ANDROID_START_X + 1 + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player HotBar
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }

        // Android Hands
        addSlot(new Slot(container, 0 , ANDROID_START_X + 1, PLAYER_START_Y + 3 * 18 + 5));
        addSlot(new Slot(container, 1 , ANDROID_START_X + 1 + 18, PLAYER_START_Y + 3 * 18 + 5));
    }

    public static AndroidMenu ofBrain(int id, PlayerInventory inventory, AndroidBrain brain) {
        return new AndroidMenu(
                id, player -> true, brain.getAndroid().getComputer().getFamily(), brain.getAndroid().getComputer().getOrCreateServerComputer(), null, inventory, brain.getAndroid().inventory, new HandContainer(brain.getAndroid())
        );
    }

    public static AndroidMenu ofData(int id, PlayerInventory inv, ComputerContainerData data) {
        return new AndroidMenu(id, player -> true, data.family(), null, data, inv, new SimpleInventory(11), new SimpleInventory(2));
    }

    private ItemStack tryItemMerge(PlayerEntity player, int slotNum, int firstSlot, int lastSlot, boolean reverse) {
        var slot = slots.get(slotNum);
        var originalStack = ItemStack.EMPTY;
        if (slot.hasStack()) {
            var clickedStack = slot.getStack();
            originalStack = clickedStack.copy();
            if (!insertItem(clickedStack, firstSlot, lastSlot, reverse)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (clickedStack.getCount() != originalStack.getCount()) {
                slot.onTakeItem(player, clickedStack);
            } else {
                return ItemStack.EMPTY;
            }
        }
        return originalStack;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        if (slot >= 0 && slot < 9) {
            return tryItemMerge(player, slot, 16, 45, true);
        } else if (slot >= 9) {
            return tryItemMerge(player, slot, 0, 9, false);
        }
        return ItemStack.EMPTY;
    }
}
