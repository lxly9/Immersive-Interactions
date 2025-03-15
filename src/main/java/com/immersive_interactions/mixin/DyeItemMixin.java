package com.immersive_interactions.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class DyeItemMixin {
    @WrapMethod(method = "useOnBlock")
    private ActionResult mod_id$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        if (itemStack.getItem() instanceof PickaxeItem) {
        }
        else if (itemStack.isIn(ItemTags.PICKAXES)) {
        }
        return original.call(context);
    }
}
