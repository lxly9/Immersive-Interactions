package com.immersive_interactions.mixin;

import com.immersive_interactions.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static com.immersive_interactions.ImmersiveInteractions.*;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Unique
    private static final ThreadLocal<Boolean> REROUTING = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("HEAD"), cancellable = true
    )
    private static void rerouteDrops1(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if (tryReroute(state, world, pos, null, null, ItemStack.EMPTY)) ci.cancel();
    }

    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V",
            at = @At("HEAD"), cancellable = true
    )
    private static void rerouteDrops2(BlockState state, WorldAccess world, BlockPos pos, BlockEntity blockEntity, CallbackInfo ci) {
        if (tryReroute(state, (World) world, pos, null, null, ItemStack.EMPTY)) ci.cancel();
    }

    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true
    )
    private static void rerouteDrops4(BlockState state, World world, BlockPos pos, BlockEntity be, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (tryReroute(state, world, pos, be, entity, tool)) ci.cancel();
    }

    @Inject(method = "onDestroyedByExplosion", at = @At("HEAD"), cancellable = true)
    private static void rerouteDrops5(World world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
        if (tryReroute(world.getBlockState(pos), world, pos, world.getBlockEntity(pos), null, ItemStack.EMPTY)) ci.cancel();
    }

    @Unique
    private static boolean tryReroute(BlockState state, World world, BlockPos pos, BlockEntity be, Entity entity, ItemStack tool) {

        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String path = blockId.getPath();

        if (REROUTING.get()) return false;

        if (isOxidizable(block.getClass())) {

            if (state.contains(DoorBlock.HALF) && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                return false;
            }

            REROUTING.set(true);
            try {

                int degradation = 0;
                if (path.contains("exposed")) degradation = 1;
                else if (path.contains("weathered")) degradation = 2;
                else if (path.contains("oxidized")) degradation = 3;

                if (degradation > 0) {
                    Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA, degradation));
                }

                Block.dropStacks(Oxidizable.getUnaffectedOxidationBlock(block).getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        else if (hasUnmossedVariant(path)) {
            REROUTING.set(true);
            try {
                Block unmossedID = Registries.BLOCK.get(Identifier.of(path.replace("mossy_", "")));
                LOGGER.info(String.valueOf(unmossedID));

                Block.dropStacks(unmossedID.getStateWithProperties(state), world, pos, be, entity, tool);
                Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        else if (hasUncrackedVariant(path)) {
            REROUTING.set(true);
            try {
                Block uncrackedID = Registries.BLOCK.get(Identifier.of(path.replace("cracked_", "")));

                Block.dropStacks(uncrackedID.getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        else if (hasUnchiseledVariant(path)) {
            REROUTING.set(true);
            try {
                Block unchiseledId = Registries.BLOCK.get(Identifier.of(path.replace("chiseled_", "")));

                Block.dropStacks(unchiseledId.getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        return false;
    }
}
