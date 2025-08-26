package com.immersive_interactions.mixin;

import com.immersive_interactions.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static com.immersive_interactions.ImmersiveInteractions.*;

@Mixin(Block.class)
public abstract class BlockMixin {


    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void addProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        Block self = (Block)(Object)this;

    }

    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void dropItems(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (!world.isClient && !player.isCreative()) {
            Block block = state.getBlock();
            var id = Registries.BLOCK.getId(block);
            String path = id.getPath();

        }
    }

    @Unique
    private static final ThreadLocal<Boolean> REROUTING = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("HEAD"), cancellable = true
    )
    private static void rerouteCopperDrops3(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if (tryReroute(state, world, pos, null, null, ItemStack.EMPTY)) ci.cancel();
    }

    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true
    )
    private static void rerouteCopperDrops6(BlockState state, World world, BlockPos pos, BlockEntity be, Entity entity, ItemStack tool, CallbackInfo ci) {
        if (tryReroute(state, world, pos, be, entity, tool)) ci.cancel();
    }

    @Unique
    private static boolean tryReroute(BlockState state, World world, BlockPos pos, BlockEntity be, Entity entity, ItemStack tool) {

        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String path = blockId.getPath();

        if (REROUTING.get()) return false;

        if (isOxidizable(block.getClass())) {
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
        if (hasUnmossedVariant(path, blockId)) {
            REROUTING.set(true);
            try {
                Block unmossedID = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("mossy_", "")));

                Block.dropStacks(unmossedID.getStateWithProperties(state), world, pos, be, entity, tool);
                Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        if (hasUncrackedVariant(path, blockId)) {
            REROUTING.set(true);
            try {
                Block uncrackedID = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("cracked_", "")));

                Block.dropStacks(uncrackedID.getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        if (hasUnchiseledVariant(path, blockId)) {
            REROUTING.set(true);
            try {
                Block unchiseledId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("chiseled_", "")));

                Block.dropStacks(unchiseledId.getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        }
        return false;
    }
}
