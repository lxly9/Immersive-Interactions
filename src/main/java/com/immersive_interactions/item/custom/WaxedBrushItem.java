package com.immersive_interactions.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

import static com.immersive_interactions.util.ModProperties.*;

public class WaxedBrushItem extends HoneycombItem  {
    public WaxedBrushItem(Settings settings) {
        super(settings);
    }
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        BlockState defaultState = block.getDefaultState();
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (defaultState.contains(WAXED) && !state.get(WAXED)) {
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
            }
            if (!world.isClient){
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
            }
            world.setBlockState(blockPos, block.getStateWithProperties(state).with(WAXED, true), 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, state));
            world.syncWorldEvent(playerEntity, 3003, blockPos, 0);
            return ActionResult.success(!state.get(WAXED));
        }
        return ActionResult.PASS;
    }
}
