package com.lilys_vanilla.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;

import com.lilys_vanilla.item.ModItems;
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

        if (!world.isClient && state.getBlock() instanceof Oxidizable) {
            Oxidizable oxidizable = (Oxidizable) state.getBlock();
            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();

            if (oxidationLevel.ordinal() > 0) {
                Block.dropStack((ServerWorld) world, pos, new ItemStack(ModItems.COPPER_PATINA));
            }
        }
    }
}

