package com.immersive_interactions.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.immersive_interactions.ImmersiveInteractions.getMinecartByName;

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
    private static AbstractMinecartEntity getMineCartEntity(MinecartEntity oldCart, World world, Block block) {
        float yaw = oldCart.getYaw();
        Vec3d velocity = oldCart.getVelocity();
        double x = oldCart.getX();
        double y = oldCart.getY();
        double z = oldCart.getZ();

        String mineCartEntity = Registries.BLOCK.getId(block).getPath() + "_minecart";
        EntityType<?> type = getMinecartByName(mineCartEntity);

        Entity entity = type.create(world);
        if (!(entity instanceof AbstractMinecartEntity newCart)) {
            return oldCart;
        }

        newCart.refreshPositionAndAngles(x, y, z, yaw, 0.0f);
        newCart.setVelocity(velocity);

        return newCart;
    }

}
