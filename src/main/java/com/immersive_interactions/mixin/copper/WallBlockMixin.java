package com.immersive_interactions.mixin.copper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallBlock.class)
public class WallBlockMixin extends Block {
    public WallBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        super.appendProperties(builder);
    }
}
