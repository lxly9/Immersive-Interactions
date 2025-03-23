package com.immersive_interactions.item;

import com.immersive_interactions.ImmersiveInteractions;
import com.immersive_interactions.item.custom.ChiselItem;
import com.immersive_interactions.item.custom.PatinaItem;
import com.immersive_interactions.item.custom.WaxedBrushItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item COPPER_PATINA = registerItem("copper_patina", new PatinaItem(new Item.Settings()));
    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(128)));
    public static final Item MOSS_CLUMP = registerItem("moss_clump", new Item(new Item.Settings()));
    public static final Item BARK = registerItem("bark", new Item(new Item.Settings()));
    public static final Item WAXED_BRUSH = registerItem("waxed_brush", new WaxedBrushItem(new Item.Settings().maxDamage(128).recipeRemainder(Items.BRUSH)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ImmersiveInteractions.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ImmersiveInteractions.LOGGER.info("Registering " + ImmersiveInteractions.MOD_ID + "Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(COPPER_PATINA);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(CHISEL);
            entries.add(WAXED_BRUSH);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(MOSS_CLUMP);
        });
    }
}
