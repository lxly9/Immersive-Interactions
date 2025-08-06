package com.immersive_interactions.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
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
}





