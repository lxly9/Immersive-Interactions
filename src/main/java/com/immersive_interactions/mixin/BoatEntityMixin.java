package com.immersive_interactions.mixin;

import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ChestBlock) {
            BoatEntity boat = (BoatEntity) (Object) this;
            World world = boat.getWorld();


            if (boat instanceof ChestBoatEntity){
                cir.setReturnValue(ActionResult.PASS);
                return;
            }

            if (!world.isClient) {
                ChestBoatEntity chestBoat = getChestBoatEntity(boat, world);

                boat.discard();
                world.spawnEntity(chestBoat);

                if (!player.getAbilities().creativeMode) {
                    heldItem.decrement(1);
                }
            }
            world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, boat.getPos(), GameEvent.Emitter.of(player));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Unique
    private static @NotNull ChestBoatEntity getChestBoatEntity(BoatEntity boat, World world) {
        BoatEntity.Type variant = boat.getVariant();
        float yaw = boat.getYaw();
        Vec3d velocity = boat.getVelocity();

        ChestBoatEntity chestBoat = new ChestBoatEntity(world, boat.getX(), boat.getY(), boat.getZ());
        chestBoat.setVariant(variant);
        chestBoat.setYaw(yaw);
        chestBoat.setVelocity(velocity);
        return chestBoat;
    }
}



