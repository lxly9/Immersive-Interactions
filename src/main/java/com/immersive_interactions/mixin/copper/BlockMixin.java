package com.immersive_interactions.mixin.copper;

import com.immersive_interactions.item.ModItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
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


    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void addProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        Block self = (Block)(Object)this;

        if (this instanceof Oxidizable) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }

        if (isModLoaded("copperrails") && isInstanceOf(this, "com.copperrails.blockId.OxidizableCopperRailBlock")) {
            builder.add(DEGRADATION);
            builder.add(WAXED);
        }

//        if (!path.startsWith("cracked_")) {
//            Identifier crackedId = Identifier.of(blockId.getNamespace(), "cracked_" + path);
//            if (Registries.BLOCK.containsId(crackedId)) {
//                builder.add(CRACKED);
//            }
//        }
    }

    @WrapMethod(method = "onPlaced")
    private void changePlacedBlock(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, Operation<Void> original) {
        Block block = state.getBlock();
        if (isOxidizable(block.getClass())){
            BlockState replacement = getCopper(state, block);
            String newCopper = replacement.toString();

            if (!world.isClient  && !newCopper.matches(".*(exposed_|weathered_|oxidized_|waxed_|ore|raw).*")) {
                world.setBlockState(pos, replacement);
            }
        }
    }

    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void dropItems(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (!world.isClient && !player.isCreative()) {
            if (state.contains(DEGRADATION)) {
            int degradation = state.get(DEGRADATION);

                if (degradation > 0) {
                    Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA, degradation));
                }
            }
            if (state.getBlock().getName().toString().contains("mossy")) {
                Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
            }
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





