package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MinecartItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static com.immersive_interactions.ImmersiveInteractions.joinWithAnd;

public class DynamicEntityTooltipHelper {

    private static List<Text> cachedBoatBlocks = null;
    private static List<Text> cachedMinecartBlocks = null;

    public static void addEntityInteractionTooltip(ItemStack stack, List<Text> tooltip) {
        if (!(stack.getItem() instanceof BoatItem || stack.getItem() instanceof MinecartItem)) return;

        boolean isBoat = stack.getItem() instanceof BoatItem;
        List<Text> interactableBlocks;

        if (isBoat) {
            if (cachedBoatBlocks == null) cachedBoatBlocks = generateInteractableBlocks(true);
            interactableBlocks = cachedBoatBlocks;
        } else {
            if (cachedMinecartBlocks == null) cachedMinecartBlocks = generateInteractableBlocks(false);
            interactableBlocks = cachedMinecartBlocks;
        }

        if (!interactableBlocks.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.immersive_interactions.can_be_interacted_with")
                    .formatted(Formatting.ITALIC, Formatting.DARK_GRAY)
                    .append(" ")
                    .append(joinWithAnd(interactableBlocks)));
        }
    }

    private static List<Text> generateInteractableBlocks(boolean isBoat) {
        List<Text> result = new ArrayList<>();

        for (Block block : Registries.BLOCK) {
            String entityName = Registries.BLOCK.getId(block).getPath() + (isBoat ? "_boat" : "_minecart");

            if ((isBoat && com.immersive_interactions.ImmersiveInteractions.getBoatEntityByName(entityName) != null)
                    || (!isBoat && com.immersive_interactions.ImmersiveInteractions.getMinecartByName(entityName) != null)) {
                result.add(Text.translatable(block.getTranslationKey()));
            }
        }

        return result;
    }
}
