package com.immersive_interactions.datagen;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MOSS_BLOCK)
                .pattern("mm")
                .pattern("mm")
                .input('m', ModItems.MOSS_CLUMP)
                .group("multi_bench")
                .criterion(FabricRecipeProvider.hasItem(Items.MOSS_BLOCK), FabricRecipeProvider.conditionsFromItem(Items.MOSS_BLOCK))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MOSS_CARPET, 2)
                .pattern("mm")
                .input('m', ModItems.MOSS_CLUMP)
                .group("multi_bench")
                .criterion(FabricRecipeProvider.hasItem(Items.MOSS_CARPET), FabricRecipeProvider.conditionsFromItem(Items.MOSS_CARPET))
                .offerTo(recipeExporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModItems.MOSS_CLUMP, 4)
                .input(Items.MOSS_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(Items.MOSS_BLOCK), FabricRecipeProvider.conditionsFromItem(Items.MOSS_BLOCK))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BOWL)
                .pattern("b b")
                .pattern(" b ")
                .input('b', ModItems.BARK)
                .group("multi_bench")
                .criterion(FabricRecipeProvider.hasItem(Items.BOWL), FabricRecipeProvider.conditionsFromItem(Items.BOWL))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.CHISEL)
                .pattern(" i ")
                .pattern(" c ")
                .pattern(" s ")
                .input('i', Items.IRON_INGOT)
                .input('c', Items.COPPER_INGOT)
                .input('s', Items.STICK)
                .group("multi_bench")
                .criterion(FabricRecipeProvider.hasItem(ModItems.CHISEL), FabricRecipeProvider.conditionsFromItem(ModItems.CHISEL))
                .offerTo(recipeExporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.WAXED_BRUSH)
                .input(Items.BRUSH)
                .input(Items.HONEYCOMB)
                .input(Items.HONEYCOMB)
                .input(Items.HONEYCOMB)
                .criterion(FabricRecipeProvider.hasItem(ModItems.WAXED_BRUSH), FabricRecipeProvider.conditionsFromItem(ModItems.WAXED_BRUSH))
                .offerTo(recipeExporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.STICK, 2)
                .input(ModItems.BARK)
                .criterion(FabricRecipeProvider.hasItem(Items.STICK), FabricRecipeProvider.conditionsFromItem(Items.STICK))
                .offerTo(recipeExporter);

    }
}
