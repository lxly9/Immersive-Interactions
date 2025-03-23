package com.immersive_interactions.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBoatEntity.class)
public abstract class ChestBoatItemMixin {

    @Inject(method = "killAndDropSelf", at = @At("HEAD"), cancellable = true)
    private void killAndDropSelf(DamageSource source, CallbackInfo ci) {
        ChestBoatEntity chestBoat = (ChestBoatEntity) (Object) this;
        String variantString = chestBoat.asItem().toString().toLowerCase().replace("chest_", "");

        chestBoat.dropItem(Registries.ITEM.get(Identifier.of(variantString)));
        chestBoat.dropItem(Items.CHEST);
        chestBoat.discard();
        ci.cancel();
    }
}
