package com.immersive_interactions.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import com.immersive_interactions.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.immersive_interactions.ImmersiveInteractions.*;
import static com.immersive_interactions.util.ModProperties.*;


@Mixin(AxeItem.class)
public class AxeItemMixin {

    @WrapMethod(method = "useOnBlock")
    private ActionResult injectScrape(ItemUsageContext context, Operation<ActionResult> original) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Block block = state.getBlock();
        int degradation = state.get(DEGRADATION);
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();


        if (!world.isClient && isOxidizable(block.getClass()) && player != null) {
//            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();
            if (!state.get(WAXED) && degradation > 0){
                if (!player.isCreative()) {
                    Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA));
                }
                BlockState newState = block.getStateWithProperties(state).with(DEGRADATION, degradation -1);
                world.setBlockState(pos, newState);
                return ActionResult.success(degradation > 0);
            }

            if (state.get(WAXED)){
                BlockState newState = block.getDefaultState().with(WAXED, false);
                world.setBlockState(pos, newState);
                return ActionResult.success(state.get(WAXED));
            }

//            if (!player.isCreative() && !blockIdString.contains("waxed") && oxidationLevel.ordinal() > 0)  {
//                Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA));
//            }
        }
        return original.call(context);
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void injectStripping(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);


        if (!world.isClient && state.isIn(BlockTags.LOGS) && player != null) {

            if (!player.isCreative() && !blockIdString.contains("stripped"))  {
                if (isModLoaded("farmersdelight")){
                    Block.dropStack(world, pos, new ItemStack(barkFD));
                }
                else {
                    Block.dropStack(world, pos, new ItemStack(ModItems.BARK));
                }
            }
        }
    }
}
