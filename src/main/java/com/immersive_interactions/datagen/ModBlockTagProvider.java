package com.immersive_interactions.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
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
    public static final TagKey<Block> CHISELED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "chiseled_blocks"));
    public static final TagKey<Block> MOSSABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "mossable_blocks"));
    public static final TagKey<Block> MOSSY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "mossy_blocks"));
    public static final TagKey<Block> CRACKABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "crackable_blocks"));
    public static final TagKey<Block> CRACKED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "cracked_blocks"));
    public static final TagKey<Block> SLIMABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimable_blocks"));
    public static final TagKey<Block> SLIMY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimy_blocks"));
    public static final TagKey<Block> AMETHYSTABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethystable_blocks"));
    public static final TagKey<Block> AMETHYST_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethyst_blocks"));
    public static final TagKey<Block> DYEABLE_BASE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "dyeable_base_blocks"));
    public static final TagKey<Block> DYED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "dyed_blocks"));
    
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(CHISELABLE_BLOCKS)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.INFESTED_STONE_BRICKS)
                .add(Blocks.COBBLED_DEEPSLATE)
                .add(Blocks.TUFF)
                .add(Blocks.TUFF_BRICKS)
                .add(Blocks.SANDSTONE)
                .add(Blocks.RED_SANDSTONE)
                .add(Blocks.NETHER_BRICKS)
                .add(Blocks.POLISHED_BLACKSTONE_BRICKS)
                .add(Blocks.QUARTZ_BLOCK)
                .add(Blocks.COPPER_BLOCK)
                .add(Blocks.EXPOSED_COPPER)
                .add(Blocks.WEATHERED_COPPER)
                .add(Blocks.OXIDIZED_COPPER)
                .add(Blocks.WAXED_COPPER_BLOCK)
                .add(Blocks.WAXED_EXPOSED_COPPER)
                .add(Blocks.WAXED_WEATHERED_COPPER)
                .add(Blocks.WAXED_OXIDIZED_COPPER);

        getOrCreateTagBuilder(CHISELED_BLOCKS)
                .add(Blocks.CHISELED_STONE_BRICKS)
                .add(Blocks.INFESTED_CHISELED_STONE_BRICKS)
                .add(Blocks.CHISELED_DEEPSLATE)
                .add(Blocks.CHISELED_TUFF)
                .add(Blocks.CHISELED_TUFF_BRICKS)
                .add(Blocks.CHISELED_SANDSTONE)
                .add(Blocks.CHISELED_RED_SANDSTONE)
                .add(Blocks.CHISELED_NETHER_BRICKS)
                .add(Blocks.CHISELED_POLISHED_BLACKSTONE)
                .add(Blocks.CHISELED_QUARTZ_BLOCK)
                .add(Blocks.CHISELED_COPPER)
                .add(Blocks.EXPOSED_CHISELED_COPPER)
                .add(Blocks.WEATHERED_CHISELED_COPPER)
                .add(Blocks.OXIDIZED_CHISELED_COPPER)
                .add(Blocks.WAXED_CHISELED_COPPER)
                .add(Blocks.WAXED_EXPOSED_CHISELED_COPPER)
                .add(Blocks.WAXED_WEATHERED_CHISELED_COPPER)
                .add(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);

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

        getOrCreateTagBuilder(DYEABLE_BASE_BLOCKS)
                .add(Blocks.WHITE_BED)
                .add(Blocks.WHITE_WOOL)
                .add(Blocks.WHITE_CARPET)
                .add(Blocks.TERRACOTTA)
                .add(Blocks.WHITE_CONCRETE)
                .add(Blocks.WHITE_CONCRETE_POWDER)
                .add(Blocks.WHITE_GLAZED_TERRACOTTA)
                .add(Blocks.GLASS)
                .add(Blocks.GLASS_PANE)
                .add(Blocks.WHITE_CANDLE)
                .add(Blocks.WHITE_CANDLE);

        getOrCreateTagBuilder(DYED_BLOCKS)
                .add(Blocks.RED_BED)
                .add(Blocks.RED_WOOL)
                .add(Blocks.RED_CARPET)
                .add(Blocks.RED_TERRACOTTA)
                .add(Blocks.RED_CONCRETE)
                .add(Blocks.RED_CONCRETE_POWDER)
                .add(Blocks.RED_GLAZED_TERRACOTTA)
                .add(Blocks.RED_STAINED_GLASS)
                .add(Blocks.RED_STAINED_GLASS_PANE)
                .add(Blocks.RED_CANDLE);
    }
}
