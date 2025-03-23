package com.immersive_interactions.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
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

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void injectScrape(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();


        if (!world.isClient && state.getBlock() instanceof Oxidizable && player != null) {
            Oxidizable oxidizable = (Oxidizable) state.getBlock();
            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();

            if (!player.isCreative() && !blockIdString.contains("waxed") && oxidationLevel.ordinal() > 0)  {
                Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA));
            }
        }
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


        if (!world.isClient && state.isIn(BlockTags.LOGS) && player != null) {

            if (!player.isCreative() && !blockIdString.contains("stripped"))  {
                Block.dropStack(world, pos, new ItemStack(ModItems.BARK));
            }
        }
    }
}
