package com.immersive_interactions.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.immersive_interactions.ImmersiveInteractions.getItemByName;

@Mixin (VehicleEntity.class)
public class VehicleEntityMixin {

    @Inject(method = "killAndDropItem", at = @At("HEAD"), cancellable = true)
    private void killAndDropItem(Item selfAsItem, CallbackInfo ci) {
        VehicleEntity vehicleEntity = (VehicleEntity) (Object) this;
        String blockInCart = Registries.ITEM.getId(selfAsItem).getPath().replace("_minecart", "");
        vehicleEntity.kill();
        vehicleEntity.dropItem(getItemByName(blockInCart));
        if (vehicleEntity instanceof AbstractMinecartEntity) {
            vehicleEntity.dropItem(Items.MINECART);
        } else {
            vehicleEntity.dropItem(selfAsItem);
        }
        ci.cancel();
    }
}
