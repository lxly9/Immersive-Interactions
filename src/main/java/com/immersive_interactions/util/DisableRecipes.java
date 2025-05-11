package com.immersive_interactions.util;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class DisableRecipes {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> killRecipes(server));
    }

    public static void killRecipes(MinecraftServer server) {
        LOGGER.info("Filtering crafting, smelting, and grindstone recipes...");

        RecipeManager recipeManager = server.getRecipeManager();
        List<RecipeEntry<?>> recipesToRemove = new ArrayList<>();

        for (RecipeEntry<?> recipeEntry : recipeManager.values()) {
            Recipe<?> recipe = recipeEntry.value();

            if (isTargetedRecipeType(recipe) && shouldRemoveRecipe(recipe, server)) {
                recipesToRemove.add(recipeEntry);
                LOGGER.info("Disabling recipe: " + recipeEntry.id());
            }
        }

        LOGGER.info("Found {} recipes to disable.", recipesToRemove.size());

        for (RecipeEntry<?> recipeEntry : recipesToRemove) {
            String recipeName = recipeEntry.id().getPath();
            String modId = recipeEntry.id().getNamespace();
        }
    }

    private static boolean isTargetedRecipeType(Recipe<?> recipe) {
        return recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe || recipe instanceof AbstractCookingRecipe || recipe instanceof SmithingRecipe;
    }

    private static boolean shouldRemoveRecipe(Recipe<?> recipe, MinecraftServer server) {
        ItemStack output = recipe.getResult(server.getRegistryManager());
        if (output != null && isBlockInTag(output.getItem())) {
            return true;
        }
        return containsTaggedBlockIngredient(recipe.getIngredients());
    }

    private static boolean containsTaggedBlockIngredient(DefaultedList<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (isBlockInTag(stack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isBlockInTag(Item item) {
        if (item instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().getDefaultState();
            return state.isIn(ModBlockTagProvider.CHISELED_BLOCKS) || state.isIn(ModBlockTagProvider.CRACKED_BLOCKS);
        }
        return false;
    }
}
