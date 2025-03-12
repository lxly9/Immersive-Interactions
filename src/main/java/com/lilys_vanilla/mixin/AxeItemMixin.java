package com.lilys_vanilla.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.block.Block;

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
        PlayerEntity player = context.getPlayer();

        if (!world.isClient && state.getBlock() instanceof Oxidizable && player != null) {
            Oxidizable oxidizable = (Oxidizable) state.getBlock();
            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();

            if (oxidationLevel.ordinal() > 0) {
                if (!player.isCreative()) {
                    Vec3d blockCenter = pos.toCenterPos();
                    Vec3d playerPos = player.getPos();
                    Vec3d direction = playerPos.subtract(blockCenter).normalize().multiply(0.55);
                    Vec3d dropPos = blockCenter.add(direction);
                    Block.dropStack(world, BlockPos.ofFloored(dropPos), new ItemStack(ModItems.COPPER_PATINA));
                }
            }
        }
    }
}
