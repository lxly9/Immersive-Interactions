package com.lilys_vanilla.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final TagKey<Block> CHISELABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "chiselable_blocks"));
    public static final TagKey<Block> WEATHERABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "weatherable_blocks"));
    public static final TagKey<Block> MOSSABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "mossable_blocks"));
    public static final TagKey<Block> MOSSY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("lilys_vanilla", "mossy_blocks"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(MOSSY_BLOCKS)
                .add(Blocks.MOSSY_COBBLESTONE)
                .add(Blocks.MOSSY_COBBLESTONE_STAIRS)
                .add(Blocks.MOSSY_COBBLESTONE_SLAB)
                .add(Blocks.MOSSY_COBBLESTONE_WALL)
                .add(Blocks.MOSSY_STONE_BRICKS)
                .add(Blocks.MOSSY_STONE_BRICK_STAIRS)
                .add(Blocks.MOSSY_STONE_BRICK_SLAB)
                .add(Blocks.MOSSY_STONE_BRICK_WALL);

        getOrCreateTagBuilder(MOSSABLE_BLOCKS)
                .add(Blocks.COBBLESTONE)
                .add(Blocks.COBBLESTONE_STAIRS)
                .add(Blocks.COBBLESTONE_SLAB)
                .add(Blocks.COBBLESTONE_WALL)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.STONE_BRICK_STAIRS)
                .add(Blocks.STONE_BRICK_SLAB)
                .add(Blocks.STONE_BRICK_WALL);
    }
}
