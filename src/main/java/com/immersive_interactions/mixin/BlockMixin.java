package com.immersive_interactions.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.immersive_interactions.util.ModProperties.*;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void addDegradationProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if ((Object) this instanceof OxidizableBlock) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setDegradationDefaultState(AbstractBlock.Settings settings, CallbackInfo ci) {
        if ((Object) this instanceof OxidizableBlock block) {
            BlockState defaultState = block.getDefaultState().with(DEGRADATION, 0).with(WAXED, false);
            ((BlockAccessor) block).callSetDefaultState(defaultState);
        }
    }
}





