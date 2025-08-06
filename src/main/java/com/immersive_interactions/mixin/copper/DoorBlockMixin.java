package com.immersive_interactions.mixin.copper;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoorBlock.class)
public class DoorBlockMixin extends Block {
    public DoorBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        super.appendProperties(builder);
    }
}
