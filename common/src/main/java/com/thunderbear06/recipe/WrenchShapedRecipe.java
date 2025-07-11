package com.thunderbear06.recipe;

import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.recipe.CustomShapedRecipe;
import dan200.computercraft.shared.recipe.ShapedRecipeSpec;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class WrenchShapedRecipe extends CustomShapedRecipe {
	public WrenchShapedRecipe(Identifier id, ShapedRecipeSpec recipe) {
		super(id, recipe);
	}

	@Override
	public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
		DefaultedList<ItemStack> remainder = super.getRemainder(inventory);
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == ItemRegistry.WRENCH.get())
			{
				stack.setDamage(stack.getDamage()+1);
				if (stack.getDamage() < stack.getMaxDamage())
					remainder.set(i, stack.copyWithCount(1));
			}
		}
		return remainder;
	}

	@Override
	public RecipeSerializer<WrenchShapedRecipe> getSerializer() {
		return RecipeRegistry.WRENCH_SHAPED.get();
	}
}
