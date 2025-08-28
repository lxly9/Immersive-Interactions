package com.immersive_interactions.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.immersive_interactions.ImmersiveInteractions.getBoatEntityByName;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.getItem() instanceof BlockItem blockItem) {
            BoatEntity oldBoat = (BoatEntity) (Object) this;
            World world = oldBoat.getWorld();

            if (!world.isClient) {
                float yaw = oldBoat.getYaw();
                Vec3d velocity = oldBoat.getVelocity();
                double x = oldBoat.getX();
                double y = oldBoat.getY();
                double z = oldBoat.getZ();

                String boatEntityName = Registries.BLOCK.getId(blockItem.getBlock()).getPath() + "_boat";
                EntityType<?> type = getBoatEntityByName(boatEntityName);

                if (type != null) {
                    Entity entity = type.create(world);
                    if (!(entity instanceof BoatEntity newBoat)) {
                        cir.setReturnValue(ActionResult.PASS);
                        return;
                    }

                    newBoat.setVariant(oldBoat.getVariant());

                    newBoat.refreshPositionAndAngles(x, y, z, yaw, 0.0f);
                    newBoat.setVelocity(velocity);

                    oldBoat.discard();
                    world.spawnEntity(newBoat);

                    itemStack.decrementUnlessCreative(1, player);

                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, oldBoat.getPos(), GameEvent.Emitter.of(player));
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}