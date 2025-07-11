package com.thunderbear06.recipe;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.recipe.CustomShapedRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.RegistryKeys;

public class RecipeRegistry {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_TYPES = DeferredRegister.create(CCAndroids.MOD_ID, RegistryKeys.RECIPE_SERIALIZER);

	public static final RegistrySupplier<RecipeSerializer<WrenchShapedRecipe>> WRENCH_SHAPED = RECIPE_TYPES.register("wrench_shaped", () -> CustomShapedRecipe.serialiser(WrenchShapedRecipe::new));

	public static void register() {
		RECIPE_TYPES.register();
		CCAndroids.LOGGER.info("Registered Recipes");
	}
}
