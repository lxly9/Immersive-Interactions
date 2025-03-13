package com.lilys_vanilla.item;

import com.lilys_vanilla.LilysVanilla;
import com.lilys_vanilla.item.custom.ChiselItem;
import com.lilys_vanilla.item.custom.MossItem;
import com.lilys_vanilla.item.custom.PatinaItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item COPPER_PATINA = registerItem("copper_patina", new PatinaItem(new Item.Settings()));
    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(128)));
    public static final Item MOSS_CLUMP = registerItem("moss_clump", new MossItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(LilysVanilla.MOD_ID, name), item);
    }

    public static void registerModItems() {
        LilysVanilla.LOGGER.info("Registering " + LilysVanilla.MOD_ID + "Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(COPPER_PATINA);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(CHISEL);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(MOSS_CLUMP);
        });
    }
}
