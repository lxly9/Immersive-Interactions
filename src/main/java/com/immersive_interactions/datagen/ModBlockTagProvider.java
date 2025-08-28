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

    public static final TagKey<Block> SLIMABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimable_blocks"));
    public static final TagKey<Block> SLIMY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "slimy_blocks"));
    public static final TagKey<Block> AMETHYSTABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethystable_blocks"));
    public static final TagKey<Block> AMETHYST_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "amethyst_blocks"));
    public static final TagKey<Block> DYEABLE_BASE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("immersive_interactions", "dyeable_base_blocks"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

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
    }
}
