package com.lilys_vanilla.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    public static final TagKey<Block> CHISELABLE_ITEMS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "chiselable_blocks"));
    public static final TagKey<Block> WEATHERABLE_ITEMS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "weatherable_blocks"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(WEATHERABLE_ITEMS)
                .add(Blocks.COPPER_BLOCK)
                .add(Blocks.EXPOSED_COPPER)
                .add(Blocks.WEATHERED_COPPER)
                .add(Blocks.OXIDIZED_COPPER);
    }
}
