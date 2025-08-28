package com.immersive_interactions.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.immersive_interactions.ImmersiveInteractions.*;

@Mixin (VehicleEntity.class)
public class VehicleEntityMixin {

    @Inject(method = "killAndDropItem", at = @At("HEAD"), cancellable = true)
    private void killAndDropItem(Item selfAsItem, CallbackInfo ci) {
        LOGGER.info(String.valueOf(selfAsItem));
        VehicleEntity vehicleEntity = (VehicleEntity) (Object) this;

        vehicleEntity.kill();
        if (vehicleEntity instanceof AbstractMinecartEntity) {
            String blockInCart = Registries.ITEM.getId(selfAsItem).getPath().replace("_minecart", "");
            LOGGER.info(blockInCart);

            if (!(Objects.equals(blockInCart, "minecart"))){
                vehicleEntity.dropItem(getItemByName(blockInCart));
            }
            vehicleEntity.dropItem(Items.MINECART);

        } else if (vehicleEntity instanceof ChestBoatEntity) {
            BoatEntity boatEntity = (BoatEntity) vehicleEntity;
            BoatEntity.Type variant = boatEntity.getVariant();
            BoatEntity newBoat = new BoatEntity(EntityType.BOAT, boatEntity.getWorld());
            newBoat.setVariant(variant);
            Item boatItem = newBoat.asItem();

            vehicleEntity.dropItem(boatItem);
            vehicleEntity.dropItem(Items.CHEST);

        } else if (isModLoaded("supplementaries") && selfAsItem.toString().contains("cannon_boat")) {
            BoatEntity boatEntity = (BoatEntity) vehicleEntity;
            BoatEntity.Type variant = boatEntity.getVariant();
            BoatEntity newBoat = new BoatEntity(EntityType.BOAT, boatEntity.getWorld());
            newBoat.setVariant(variant);
            Item boatItem = newBoat.asItem();

            vehicleEntity.dropItem(boatItem);
            vehicleEntity.dropItem(getItemByName("cannon"));
        } else {
            vehicleEntity.dropItem(selfAsItem);
        }
        ci.cancel();
    }
}
