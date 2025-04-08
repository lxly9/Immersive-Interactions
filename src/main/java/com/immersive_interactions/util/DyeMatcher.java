package com.immersive_interactions.util;

import com.immersive_interactions.datagen.ModBlockTagProvider;

import static com.immersive_interactions.ImmersiveInteractions.MOD_ID;
import static com.immersive_interactions.util.BlockTransformationHelper.findBestMatch;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;


public class DyeMatcher {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Block dyedBlockMatcher(String blockIdString, ItemStack itemStack) {
        String newBlockId = blockIdString.replace("^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_", "");
        Block dyedBlock = findBestMatch(newBlockId, ModBlockTagProvider.DYEABLE_BASE_BLOCKS);
        assert dyedBlock != null;
        String baseDyedBlock = dyedBlock.toString().replace("white_", "");
        String newBaseDyedBlock = baseDyedBlock.replace("glass", "stained_glass");
        String dyeColor = itemStack.getItem().toString().replace("dye", "");
        String[] parts = newBaseDyedBlock.split(":");
        String newDyedBlockId = dyeColor + parts[1].replace("}","");
        return Registries.BLOCK.get(Identifier.of(newDyedBlockId));
    }
}