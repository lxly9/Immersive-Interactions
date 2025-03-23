package com.immersive_interactions.datagen;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;


public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(Blocks.CRACKED_STONE_BRICKS, Blocks.STONE_BRICKS);
        addDrop(Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICKS);
        addDrop(Blocks.CRACKED_DEEPSLATE_TILES, Blocks.DEEPSLATE_TILES);
        addDrop(Blocks.CRACKED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
        addDrop(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        addDrop(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS);
        addDrop(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS);
        addDrop(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB);
        addDrop(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.STONE_BRICK_WALL);
        addDrop(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE);
        addDrop(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS);
        addDrop(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB);
        addDrop(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL);
        addDrop(Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
        addDrop(Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
        addDrop(Blocks.CHISELED_TUFF, Blocks.TUFF);
        addDrop(Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF_BRICKS);
        addDrop(Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF_BRICKS);
        addDrop(Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
        addDrop(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        addDrop(Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
        addDrop(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
    }
}