package com.immersive_interactions.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

import static com.immersive_interactions.util.ModProperties.*;

public class PatinaItem extends Item {
    public PatinaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockState defaultState = block.getDefaultState();

        if (!world.isClient && defaultState.contains(DEGRADATION)) {
            int degradation = state.get(DEGRADATION);

            if (degradation < 3 && !state.get(WAXED)) {
                ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
                BlockState nextState = block.getStateWithProperties(state).with(DEGRADATION, degradation + 1);

                world.setBlockState(pos, nextState);
                context.getStack().decrementUnlessCreative(1, player);
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS);
                world.syncWorldEvent(WorldEvents.BLOCK_SCRAPED, pos, 0);
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
            }
            return ActionResult.success(degradation < 3);
        }else {
            return super.useOnBlock(context);
        }
    }
}
