package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static com.immersive_interactions.ImmersiveInteractions.LOGGER;


public class DyeMatcher {

    public static Block dyedBlockMatcher(Identifier blockId, Identifier identifier) {
        String dyeName1 = identifier.getPath().replace("dye","");
        String block = blockId.getPath().replaceAll("^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_","");
        if (block.equals("glass")) {
            String newDyedBlock = dyeName1 + "stained_" + block;
            LOGGER.info(newDyedBlock);
            return Registries.BLOCK.get(Identifier.of(newDyedBlock));
        }
        if (block.equals("glass_pane")) {
            String newDyedBlock = dyeName1 + "stained_" + block;
            LOGGER.info(newDyedBlock);
            return Registries.BLOCK.get(Identifier.of(newDyedBlock));
        }
        String newDyedBlock = dyeName1 + block;

        return Registries.BLOCK.get(Identifier.of(newDyedBlock));
    }
}