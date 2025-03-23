package com.immersive_interactions.datagen;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.CHISEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.COPPER_PATINA, Models.GENERATED);
        itemModelGenerator.register(ModItems.MOSS_CLUMP, Models.GENERATED);
        itemModelGenerator.register(ModItems.BARK, Models.GENERATED);
        itemModelGenerator.register(ModItems.WAXED_BRUSH, Models.GENERATED);
    }
}
