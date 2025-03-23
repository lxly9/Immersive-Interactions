package com.immersive_interactions.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartEntity.class)
public abstract class MinecartEntityMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack item = player.getStackInHand(hand);

        if (item.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            MinecartEntity mineCart = (MinecartEntity) (Object) this;
            World world = mineCart.getWorld();

            if (!world.isClient) {
                AbstractMinecartEntity newMinecart = getMineCartEntity(mineCart, world, block);
                if (newMinecart != null) {
                    mineCart.discard();
                    world.spawnEntity(newMinecart);

                    if (!player.getAbilities().creativeMode) {
                        item.decrement(1);
                    }

                    cir.setReturnValue(ActionResult.SUCCESS);
                }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, mineCart.getPos(), GameEvent.Emitter.of(player));
            }
        }
    }

    @Unique
    private static AbstractMinecartEntity getMineCartEntity(MinecartEntity mineCart, World world, Block block) {
        AbstractMinecartEntity newMinecart = null;

        float yaw = mineCart.getYaw();
        Vec3d velocity = mineCart.getVelocity();
        double x = mineCart.getX(), y = mineCart.getY(), z = mineCart.getZ();

        if (block instanceof ChestBlock) {
            newMinecart = new ChestMinecartEntity(world, x, y, z);
        } else if (block instanceof FurnaceBlock) {
            newMinecart = new FurnaceMinecartEntity(world, x, y, z);
        } else if (block instanceof HopperBlock) {
            newMinecart = new HopperMinecartEntity(world, x, y, z);
        } else if (block instanceof TntBlock) {
            newMinecart = new TntMinecartEntity(world, x, y, z);
        }

        if (newMinecart != null) {
            newMinecart.setYaw(yaw);
            newMinecart.setVelocity(velocity);
        }

        return newMinecart;
    }
}
