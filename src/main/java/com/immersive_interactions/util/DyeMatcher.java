package com.immersive_interactions.util;

import com.immersive_interactions.datagen.ModBlockTagProvider;

import static com.immersive_interactions.ImmersiveInteractions.MOD_ID;
import static com.immersive_interactions.util.BlockTransformationHelper.findBestMatch;
import static net.minecraft.component.type.DyedColorComponent.getColor;

import net.minecraft.block.Block;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DyeMatcher {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Block dyedBlockMatcher(String blockIdString, ItemStack itemStack) {
        Item item = itemStack.getItem();
        String newBlockId = blockIdString.replace("^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_", "");
        Block dyedBlock = findBestMatch(newBlockId, ModBlockTagProvider.DYEABLE_BASE_BLOCKS);
        String baseDyedBlock = dyedBlock.toString().replace("white_", "");
        String newBaseDyedBlock = baseDyedBlock.replace("glass", "stained_glass");
        LOGGER.info(newBaseDyedBlock);
        String dyeItem = itemStack.getItem().toString();
        String dyeColor = dyeItem.replace("dye", "");
        String[] parts = newBaseDyedBlock.split(":");
        String newDyedBlockId = dyeColor + parts[1].replace("}","");
        LOGGER.info(newDyedBlockId);
        return Registries.BLOCK.get(Identifier.of(newDyedBlockId));
    }
}