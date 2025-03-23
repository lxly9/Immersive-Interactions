package com.immersive_interactions.mixin;

import com.immersive_interactions.item.custom.WaxedBrushItem;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignChangingItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {

    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        if (stack.getItem() instanceof WaxedBrushItem) {
            BlockEntity signChangingItem2 = world.getBlockEntity(pos);
            if (signChangingItem2 instanceof SignBlockEntity signBlockEntity) {
                Item bl2 = stack.getItem();
                SignChangingItem signChangingItem = bl2 instanceof SignChangingItem ? (SignChangingItem) bl2 : null;
                boolean canModify = signChangingItem != null && player.canModifyBlocks();

                if (!world.isClient) {
                    if (canModify && !signBlockEntity.isWaxed() && !this.isOtherPlayerEditing(player, signBlockEntity)) {
                        boolean isFrontFacing = signBlockEntity.isPlayerFacingFront(player);
                        if (signChangingItem.canUseOnSignText(signBlockEntity.getText(isFrontFacing), player) && signChangingItem.useOnSign(world, signBlockEntity, isFrontFacing, player)) {
                            signBlockEntity.runCommandClickEvent(player, world, pos, isFrontFacing);
                            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));

                            stack.damage(1, (ServerWorld) world, (ServerPlayerEntity) player, item -> Objects.requireNonNull(player).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                            cir.setReturnValue(ItemActionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private boolean isOtherPlayerEditing(PlayerEntity player, SignBlockEntity blockEntity) {
        UUID editorId = blockEntity.getEditor();
        return editorId != null && !editorId.equals(player.getUuid());
    }
}
