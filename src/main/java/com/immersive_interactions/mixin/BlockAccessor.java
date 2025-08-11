package com.immersive_interactions.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockAccessor {@Accessor("defaultState")
    void callSetDefaultState(BlockState state);
    @Accessor("stateManager")
    StateManager<Block, ?> getStateManager();
}