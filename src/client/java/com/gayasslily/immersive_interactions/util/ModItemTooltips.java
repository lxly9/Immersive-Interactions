package com.gayasslily.immersive_interactions.util;

import com.gayasslily.immersive_interactions.datagen.ModBlockTagProvider;
import com.gayasslily.immersive_interactions.datagen.ModItemTagProvider;
import com.gayasslily.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Items;
import net.minecraft.item.SignItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.text.Text;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gayasslily.immersive_interactions.ImmersiveInteractions.isModLoaded;

public class ModItemTooltips {
    public static void register() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            Block block = Block.getBlockFromItem(itemStack.getItem());
            List<Text> variants = new ArrayList<>();
            List<Text> vehicleInteractibles = new ArrayList<>();

            if (itemStack.isOf(Items.SHEARS)) {
                if (Screen.hasShiftDown()) {
                    list.add(1, Text.translatable("tooltip.immersive_interactions.shears3"));
                } else {
                    addShift(1, list);
                }
            }
            {
                if (itemStack.isOf(ModItems.COPPER_PATINA)) {
                    if (Screen.hasShiftDown()) list.add(1, Text.translatable("tooltip.immersive_interactions.copper_patina"));
                    else addShift(1, list);
                }
                if (itemStack.isOf(ModItems.MOSS_CLUMP)) {
                    if (Screen.hasShiftDown()) list.add(1, Text.translatable("tag.item.immersive_interactions.can_apply_moss"));
                    else addShift(1, list);
                }
                if (itemStack.isOf(ModItems.CHISEL)) {
                    if (Screen.hasShiftDown()) list.add(1, Text.translatable("tooltip.immersive_interactions.chisel"));
                    else addShift(1, list);
                }
                if (itemStack.isIn(ModItemTagProvider.CAN_APPLY_BARK)) {
                    if (Screen.hasShiftDown()) list.add(1, Text.translatable("tag.item.immersive_interactions.can_apply_bark"));
                    else addShift(1, list);
                }
                if (itemStack.isIn(ModItemTagProvider.CAN_WAX_COPPER)) {
                    if (Screen.hasShiftDown()) {
                        if (isModLoaded("waxed_workstations"))
                            list.add(1, Text.translatable("tag.item.immersive_interactions.can_wax_blocks"));
                        else list.add(1, Text.translatable("tag.item.immersive_interactions.can_wax_copper"));
                    }
                    else addShift(1, list);
                }
                if (itemStack.isIn(ModItemTagProvider.CAN_APPLY_TO_MINECART)) vehicleInteractibles.add(Text.translatable("tooltip.immersive_interactions.minecart"));
                if (itemStack.isIn(ModItemTagProvider.CAN_APPLY_TO_BOAT)) vehicleInteractibles.add(Text.translatable("tooltip.immersive_interactions.boat"));

                if (block.getDefaultState().isIn(ModBlockTagProvider.CRACKABLE_BLOCKS)) variants.add(Text.translatable("tag.block.immersive_interactions.crackable_blocks"));
                if (block.getDefaultState().isIn(ModBlockTagProvider.MOSSABLE_BLOCKS)) variants.add(Text.translatable("tag.block.immersive_interactions.mossable_blocks"));
                if (block.getDefaultState().isIn(ModBlockTagProvider.CHISELABLE_BLOCKS)) variants.add(Text.translatable("tag.block.immersive_interactions.chiselable_blocks"));

                Optional<RegistryEntry<PointOfInterestType>> optional = PointOfInterestTypes.getTypeForState(block.getDefaultState());
                if (isModLoaded("waxed_workstations") && optional.isPresent() && optional.get().isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)) variants.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks"));
                if (block instanceof Oxidizable || itemStack.getItem() instanceof SignItem || itemStack.getItem() instanceof HangingSignItem) variants.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks"));
                if (block instanceof Oxidizable) variants.addLast(Text.translatable("tag.block.immersive_interactions.oxidizable_blocks"));

                if (!variants.isEmpty()) {
                    if (Screen.hasShiftDown()) list.addAll(1, variants);
                    else addShift(1, list);
                }
                if (!vehicleInteractibles.isEmpty()) {
                    if (Screen.hasShiftDown()) list.addAll(1, vehicleInteractibles);
                    else addShift(1, list);
                }
            }
        });
    }

    public static List<Text> addShift(int ordinal, List<Text> list) {
        list.add(ordinal, Text.translatable("tooltip.immersive_interactions.shift"));
        return list;
    }
}

