package com.lilys_vanilla.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class PatinaItem extends Item {
    public PatinaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block clickedBlock = state.getBlock();

        if (!world.isClient && clickedBlock instanceof Oxidizable oxidizable) {
            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();

            if (oxidationLevel.ordinal() < Oxidizable.OxidationLevel.values().length - 1) {
                BlockState nextState = oxidizable.getDegradationResult(state).orElse(state);
                world.setBlockState(pos, nextState);
                context.getStack().decrement(1);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS);
                world.syncWorldEvent(WorldEvents.BLOCK_SCRAPED, pos, 0);
            }
        }

        return ActionResult.SUCCESS;
    }
}
