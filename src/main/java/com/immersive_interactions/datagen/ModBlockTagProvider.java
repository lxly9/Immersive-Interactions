package com.immersive_interactions.datagen;

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

    public static final TagKey<Block> CHISELABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "chiselable_blocks"));
    public static final TagKey<Block> WEATHERABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "weatherable_blocks"));
    public static final TagKey<Block> MOSSABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "mossable_blocks"));
    public static final TagKey<Block> MOSSY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "mossy_blocks"));
    public static final TagKey<Block> CRACKABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "crackable_blocks"));
    public static final TagKey<Block> CRACKED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "cracked_blocks"));
    public static final TagKey<Block> SLIMABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimable_blocks"));
    public static final TagKey<Block> SLIMY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimy_blocks"));
    public static final TagKey<Block> AMETHYSTABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethystable_blocks"));
    public static final TagKey<Block> AMETHYST_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethyst_blocks"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(MOSSABLE_BLOCKS)
                .add(Blocks.COBBLESTONE)
                .add(Blocks.COBBLESTONE_STAIRS)
                .add(Blocks.COBBLESTONE_SLAB)
                .add(Blocks.COBBLESTONE_WALL)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.STONE_BRICK_STAIRS)
                .add(Blocks.STONE_BRICK_SLAB)
                .add(Blocks.STONE_BRICK_WALL)
                .add(Blocks.INFESTED_STONE_BRICKS);

        getOrCreateTagBuilder(MOSSY_BLOCKS)
                .add(Blocks.MOSSY_COBBLESTONE)
                .add(Blocks.MOSSY_COBBLESTONE_STAIRS)
                .add(Blocks.MOSSY_COBBLESTONE_SLAB)
                .add(Blocks.MOSSY_COBBLESTONE_WALL)
                .add(Blocks.MOSSY_STONE_BRICKS)
                .add(Blocks.MOSSY_STONE_BRICK_STAIRS)
                .add(Blocks.MOSSY_STONE_BRICK_SLAB)
                .add(Blocks.MOSSY_STONE_BRICK_WALL)
                .add(Blocks.INFESTED_MOSSY_STONE_BRICKS);

        getOrCreateTagBuilder(CRACKABLE_BLOCKS)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.DEEPSLATE_BRICKS)
                .add(Blocks.DEEPSLATE_TILES)
                .add(Blocks.NETHER_BRICKS)
                .add(Blocks.POLISHED_BLACKSTONE_BRICKS);

        getOrCreateTagBuilder(CRACKED_BLOCKS)
                .add(Blocks.CRACKED_STONE_BRICKS)
                .add(Blocks.CRACKED_DEEPSLATE_BRICKS)
                .add(Blocks.CRACKED_DEEPSLATE_TILES)
                .add(Blocks.CRACKED_NETHER_BRICKS)
                .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);

        getOrCreateTagBuilder(SLIMABLE_BLOCKS)
                .add(Blocks.PISTON);

        getOrCreateTagBuilder(SLIMY_BLOCKS)
                .add(Blocks.STICKY_PISTON);

        getOrCreateTagBuilder(AMETHYSTABLE_BLOCKS)
                .add(Blocks.SCULK_SENSOR);

        getOrCreateTagBuilder(AMETHYST_BLOCKS)
                .add(Blocks.CALIBRATED_SCULK_SENSOR);
    }
}
