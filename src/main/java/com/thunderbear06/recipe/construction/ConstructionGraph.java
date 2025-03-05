package com.thunderbear06.recipe.construction;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConstructionGraph {
    public ConstructionNode currentNode;
    private String currentNodeKey = "start";
    private boolean isComplete = false;

    public ConstructionGraph() {
        currentNode = ConstructionRecipeManager.RECIPE.getNode(currentNodeKey);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public @Nullable EntityType<?> getResultingEntity() {
        Optional<EntityType<?>> typeOptional = EntityType.get(currentNode.getResultEntityString());

        return typeOptional.orElse(null);
    }

    public boolean applyItemStack(ItemStack stack) {
        if (isComplete())
            return false;

        if (!currentNode.isComplete()) {
            if (currentNode.onPartApplied(stack.getItem())) {
                stack.decrement(1);

                if (currentNode.isComplete()) {
                    String key = currentNode.getNextNode(stack.getItem());

                    if (key != null)
                        navigateNode(key);
                }
                return true;
            }
        }

        return false;
    }

    private void navigateNode(String key) {
        currentNodeKey = key;
        currentNode = ConstructionRecipeManager.RECIPE.getNode(key);

        if (currentNode.isEndNode())
            isComplete = true;
    }
}
