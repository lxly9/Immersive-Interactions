package com.gayasslily.immersive_interactions.datagen;

import com.gayasslily.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }


    public static final TagKey<Item> CAN_REPAIR_BRICK = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_repair_brick"));
    public static final TagKey<Item> CAN_APPLY_MOSS = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_moss"));
    public static final TagKey<Item> CAN_APPLY_BARK = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_bark"));
    public static final TagKey<Item> CAN_WAX_COPPER = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_wax_copper"));
    public static final TagKey<Item> CAN_APPLY_TO_MINECART = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_to_minecart"));
    public static final TagKey<Item> CAN_APPLY_TO_BOAT = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_to_boat"));


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(CAN_REPAIR_BRICK)
                .add(Items.CLAY_BALL);

        getOrCreateTagBuilder(CAN_APPLY_MOSS)
                .add(Items.MOSS_CARPET)
                .add(ModItems.MOSS_CLUMP);

        getOrCreateTagBuilder(CAN_APPLY_BARK)
                .add(ModItems.BARK);

        getOrCreateTagBuilder(CAN_WAX_COPPER)
                .add(ModItems.WAXED_BRUSH)
                .add(Items.HONEYCOMB);

        getOrCreateTagBuilder(CAN_APPLY_TO_MINECART)
                .add(Blocks.CHEST.asItem())
                .add(Blocks.FURNACE.asItem());

        getOrCreateTagBuilder(CAN_APPLY_TO_BOAT)
                .add(Blocks.CHEST.asItem())
                .add(Blocks.FURNACE.asItem());
    }
}