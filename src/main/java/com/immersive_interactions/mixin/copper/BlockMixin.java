package com.immersive_interactions.mixin.copper;

import com.immersive_interactions.mixin.BlockAccessor;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.immersive_interactions.util.ModProperties.*;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void addDegradationProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (this instanceof Oxidizable) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }
    }
}





