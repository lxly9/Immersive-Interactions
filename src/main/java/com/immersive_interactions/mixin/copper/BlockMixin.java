package com.immersive_interactions.mixin.copper;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.immersive_interactions.ImmersiveInteractions.*;
import static com.immersive_interactions.util.ModProperties.*;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Shadow
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return null;
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void addDegradationProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (this instanceof Oxidizable) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }

        if (isModLoaded("copperrails") && isInstanceOf(this, "com.copperrails.block.OxidizableCopperRailBlock")) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }
    }

    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void changePlacedBlock(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        Block block = state.getBlock();
        BlockState replacement = getCopper(state, block);

        if (!world.isClient && replacement.toString().contains("copper")) {
            world.setBlockState(pos, replacement);
        }
    }

    @Unique
    public BlockState getCopper(BlockState state, Block block) {
            var id = Registries.BLOCK.getId(block);
            String path = id.getPath();

            if (path.contains("copper")) {
                boolean waxed = path.contains("waxed");

                int degradation = 0;
                if (path.contains("exposed")) degradation = 1;
                else if (path.contains("weathered")) degradation = 2;
                else if (path.contains("oxidized")) degradation = 3;

                String basePath = path
                        .replace("waxed_", "")
                        .replace("exposed_", "")
                        .replace("weathered_", "")
                        .replace("oxidized_", "");

                Block baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), basePath));

                if (baseBlock == Blocks.AIR) {
                    String altBasePath = basePath + "_block";
                    baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), altBasePath));
                }

                if (baseBlock != Blocks.AIR) {
                    return baseBlock.getStateWithProperties(state).with(WAXED, waxed).with(DEGRADATION, degradation);
                }

            }
        return state;
    }
}





