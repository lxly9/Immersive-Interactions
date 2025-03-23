package com.immersive_interactions.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
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

import static net.minecraft.item.HoneycombItem.getWaxedState;

public class WaxedBrushItem extends Item implements SignChangingItem {
    public WaxedBrushItem(Settings settings) {
        super(settings);
    }
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return (ActionResult)getWaxedState(blockState).map((state) -> {
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
            }

            if (!world.isClient){
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
            }
            world.setBlockState(blockPos, state, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, state));
            world.syncWorldEvent(playerEntity, 3003, blockPos, 0);
            return ActionResult.success(world.isClient);
        }).orElse(ActionResult.PASS);
    }
    public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
        if (signBlockEntity.setWaxed(true)) {
            world.syncWorldEvent((PlayerEntity)null, 3003, signBlockEntity.getPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    public boolean canUseOnSignText(SignText signText, PlayerEntity player) {
        return true;
    }
}
