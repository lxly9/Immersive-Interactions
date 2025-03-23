package com.immersive_interactions.mixin;

import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (VehicleEntity.class)
public class VehicleEntityMixin {

    @Inject(method = "killAndDropItem", at = @At("HEAD"), cancellable = true)
    private void killAndDropItem(Item selfAsItem, CallbackInfo ci) {
        VehicleEntity vehicleEntity = (VehicleEntity) (Object) this;
        String mineCartItem = selfAsItem.asItem().toString();
        String blockInCart = mineCartItem.replace("minecart", "");
        vehicleEntity.kill();
        if (blockInCart.contains("_")){
            vehicleEntity.dropItem(Registries.ITEM.get(Identifier.of(blockInCart.replace("_", ""))));
        }
        vehicleEntity.dropItem(Items.MINECART);
        ci.cancel();
    }
}
