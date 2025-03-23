package com.immersive_interactions.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

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
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            Oxidizable.OxidationLevel oxidationLevel = oxidizable.getDegradationLevel();

            if (oxidationLevel.ordinal() < Oxidizable.OxidationLevel.values().length - 1) {
                BlockState nextState = oxidizable.getDegradationResult(state).orElse(state);

                world.setBlockState(pos, nextState);
                context.getStack().decrementUnlessCreative(1, player);
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS);
                world.syncWorldEvent(WorldEvents.BLOCK_SCRAPED, pos, 0);
            }
            world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
            return ActionResult.success(oxidationLevel.ordinal() < 3);
        }else {
            return super.useOnBlock(context);
        }
    }
}
