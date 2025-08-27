package com.immersive_interactions.mixin;

import com.immersive_interactions.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static com.immersive_interactions.ImmersiveInteractions.*;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Unique
    private static final ThreadLocal<Boolean> REROUTING = ThreadLocal.withInitial(() -> false);

    /**
     * Reroute normal block drops from loot tables.
     */
    @Inject(method = "getDroppedStacks", at = @At("HEAD"), cancellable = true)
    private void rerouteGetDroppedStacks(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (REROUTING.get()) return;

        World world = builder.getWorld();
        Vec3d origin = builder.get(LootContextParameters.ORIGIN);
        BlockPos pos = BlockPos.ofFloored(origin);
        BlockEntity be = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        Entity entity = builder.getOptional(LootContextParameters.THIS_ENTITY);
        ItemStack tool = builder.getOptional(LootContextParameters.TOOL);

        if (tryReroute(state, world, pos, be, entity, tool)) {
            cir.setReturnValue(Collections.emptyList());
        }
    }

    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    private void rerouteOnExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci) {
        if (REROUTING.get()) return;
        if (state.getBlock().shouldDropItemsOnExplosion(explosion) && world instanceof ServerWorld serverWorld) {
            if (tryReroute(state, world, pos, world.getBlockEntity(pos), explosion.getEntity(), ItemStack.EMPTY)) {

                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                state.getBlock().onDestroyedByExplosion(world, pos, explosion);
                ci.cancel();
            }
        }
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

                if (hasUnchiseledVariant(path)) {
                    Block.dropStacks(Blocks.COPPER_BLOCK.getStateWithProperties(state), world, pos, be, entity, tool);
                    return true;
                }

                if (path.contains("waxed")) {
                    Block.dropStacks(path.replace("waxed_","").getStateWithProperties(state), world, pos, be, entity, tool);
                    return true;
                }

                Block.dropStacks(Oxidizable.getUnaffectedOxidationBlock(block).getStateWithProperties(state), world, pos, be, entity, tool);
                return true;
            } finally {
                REROUTING.set(false);
            }
        } else if (hasUnmossedVariant(path)) {
            REROUTING.set(true);
            try {Block unvariantId = getBlockVariant("mossy_", path);
                if (unvariantId != null) {
                    Block.dropStacks(unvariantId.getStateWithProperties(state), world, pos, be, entity, tool);
                    Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                    return true;
                }
            } finally {
                REROUTING.set(false);
            }
        } else if (hasUncrackedVariant(path)) {
            REROUTING.set(true);
            LOGGER.info("yes");
            try {Block unvariantId = getBlockVariant("cracked_", path);
                if (unvariantId != null) {
                    Block.dropStacks(unvariantId.getStateWithProperties(state), world, pos, be, entity, tool);
                    return true;
                }
            } finally {
                REROUTING.set(false);
            }
        } else if (hasUnchiseledVariant(path)) {
            REROUTING.set(true);
            try {Block unvariantId = getBlockVariant("chiseled_", path);
                if (unvariantId != null && !state.get(Properties.SLOT_0_OCCUPIED)) {
                    Block.dropStacks(unvariantId.getStateWithProperties(state), world, pos, be, entity, tool);
                    return true;
                }
            } finally {
                REROUTING.set(false);
            }
        }
        return false;
    }
}
