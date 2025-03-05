package com.thunderbear06.recipe.construction;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ConstructionNode {
    public Item part;
    public int count;
    public int displayProgress;

    private String result;
    private boolean isEndNode = false;

    private int partsApplied = 0;

    private final HashMap<Item, String> childNodes = new HashMap<>();

    public boolean onPartApplied(Item item) {
        if (partsApplied >= count)
            return false;
        if (item.equals(part))
            partsApplied++;
        return true;
    }

    public boolean isComplete() {
        return partsApplied >= count;
    }

    public boolean isEndNode() {
        return isEndNode;
    }

    public String getResultEntityString() {
        return result;
    }

    public @Nullable String getNextNode(Item item) {
        if (!childNodes.containsKey(item))
            return null;
        return childNodes.get(item);
    }

    public void addChildNode(String nodeName, Item item) {
        childNodes.put(item,nodeName);
    }

    public int getDisplayProgress() {
        return displayProgress;
    }
}
