package com.immersive_interactions.datagen;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
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
    public static final TagKey<Item> CAN_APPLY_SLIME = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_slime"));
    public static final TagKey<Item> CAN_APPLY_AMETHYST = TagKey.of(RegistryKeys.ITEM, Identifier.of("immersive_interactions", "can_apply_amethyst"));


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(CAN_REPAIR_BRICK)
                .add(Items.CLAY_BALL);

        getOrCreateTagBuilder(CAN_APPLY_MOSS)
                .add(Items.MOSS_CARPET)
                .add(ModItems.MOSS_CLUMP);

        getOrCreateTagBuilder(CAN_APPLY_SLIME)
                .add(Items.SLIME_BALL);

        getOrCreateTagBuilder(CAN_APPLY_AMETHYST)
                .add(Items.AMETHYST_SHARD);
    }
}