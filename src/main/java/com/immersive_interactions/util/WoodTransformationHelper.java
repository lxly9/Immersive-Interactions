package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;

public class WoodTransformationHelper {
    public static Block transformLogToWood(String blockIdString) {
        if (blockIdString.contains("stripped_")) {
            String logVariant = blockIdString.replace("stripped_", "");
            return findWoodVariant(logVariant);
        } else if (blockIdString.contains("log")) {
            String woodVariant = blockIdString.replace("_log", "_wood");
            return findWoodVariant(woodVariant);
        }
        return null;
    }

    private static Block findWoodVariant(String variant) {
        RegistryEntryList<Block> blocksInTag = Registries.BLOCK.getEntryList(BlockTags.LOGS).orElse(null);
        if (blocksInTag == null) return null;

        for (RegistryEntry<Block> entry : blocksInTag) {
            Block tagBlock = entry.value();
            String tagBlockId = Registries.BLOCK.getId(tagBlock).toString();
            if (tagBlockId.endsWith(variant)) {
                return tagBlock;
            }
        }
        return null;
    }
}