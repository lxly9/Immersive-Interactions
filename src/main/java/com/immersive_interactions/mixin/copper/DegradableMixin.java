package com.immersive_interactions.mixin.copper;

import com.immersive_interactions.util.ModProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(Degradable.class)
public interface DegradableMixin<T extends Enum<T>> {
    /**
     * @author gayasslily
     * @reason yeah, had to rewrite the logic
     */
    @Overwrite
    default void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState currentState = world.getBlockState(pos);

        if (currentState.get(ModProperties.WAXED)) return;
        int degradationLevel = currentState.get(ModProperties.DEGRADATION);
        if (degradationLevel >= 3) return;

        Optional<BlockState> degraded = ((Degradable<?>) this).tryDegrade(state, world, pos, random);

        if (degraded.isPresent()) {
            BlockState updatedState = currentState.with(ModProperties.DEGRADATION, degradationLevel + 1);
            world.setBlockState(pos, updatedState);
        }
    }
}