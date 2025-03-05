package com.thunderbear06.recipe.construction;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.item.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

public class ConstructionRecipe {
    private HashMap<String, ConstructionNode> Nodes = createDefaultRecipe();

    public void register() {
        CCAndroids.LOGGER.info("Registered android construction recipe");
    }

    public ConstructionNode getNode(String nodeKey) {
        return Nodes.get(nodeKey);
    }

    private HashMap<String, ConstructionNode> createDefaultRecipe() {
        HashMap<String, ConstructionNode> recipe = new HashMap<>();

        ConstructionNode node = new ConstructionNode();
        node.part = ItemRegistry.COMPONENTS;
        node.count = 10;
        node.displayProgress = 1;
        node.addChildNode("iron", Items.IRON_INGOT);

        recipe.put("start", node);

        ConstructionNode nodeIron = new ConstructionNode();
        nodeIron.part = Items.IRON_INGOT;
        nodeIron.count = 10;
        nodeIron.displayProgress = 2;
        nodeIron.addChildNode("gold", Items.GOLD_INGOT);

        recipe.put("iron", nodeIron);

        ConstructionNode nodeGold = new ConstructionNode();
        nodeGold.part = Items.GOLD_INGOT;
        nodeGold.count = 10;
        nodeGold.displayProgress = 3;
        nodeGold.addChildNode("end", Items.GOLD_INGOT);

        recipe.put("gold", nodeGold);

        return recipe;
    }
}
