package com.immersive_interactions.util;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import static com.immersive_interactions.util.BlockTransformationHelper.findBestMatch;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DyeMatcher {
    public static Block dyedBlockMatcher(String blockIdString, ItemStack itemStack) {
        Item item = itemStack.getItem();
        String newBlockId = blockIdString.replace("^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_", "");
        Block dyedBlock = findBestMatch(newBlockId, ModBlockTagProvider.DYEABLE_BASE_BLOCKS);
        String baseDyedBlock = dyedBlock.toString().replace("white_", "");
        String dyeItem = Registries.ITEM.getId(item).toString();
        String dyeColor = dyeItem.replace("dye", "");
        String[] parts = baseDyedBlock.split(":");
        String newDyedBlockId = dyeColor + parts[1].replace("}","");
        return Registries.BLOCK.get(Identifier.of(newDyedBlockId));
    }
}