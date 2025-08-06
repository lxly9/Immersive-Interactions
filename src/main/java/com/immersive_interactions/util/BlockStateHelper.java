package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.state.StateManager;

import static com.immersive_interactions.util.ModProperties.*;

public class BlockStateHelper {
    public static void appendOxidationProperties(Block block, StateManager.Builder<Block, BlockState> builder) {
        if (block instanceof Oxidizable) {
            builder.add(DEGRADATION, WAXED);
        }
    }
}
