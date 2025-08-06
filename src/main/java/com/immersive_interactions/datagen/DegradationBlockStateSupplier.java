package com.immersive_interactions.datagen;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;

import com.google.gson.JsonObject;

public class DegradationBlockStateSupplier implements BlockStateSupplier {
    private final Block block;
    private final IntProperty degradationProperty;
    private final int maxAge;
    private final Identifier modelId;

    public DegradationBlockStateSupplier(Block block, IntProperty degradationProperty, int maxAge, Identifier modelId) {
        this.block = block;
        this.degradationProperty = degradationProperty;
        this.maxAge = maxAge;
        this.modelId = modelId;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    public void write(JsonObject json) {
        JsonObject variants = new JsonObject();

        for (int i = 0; i < maxAge; i++) {
            // Example variant key: "degradation=0"
            String variantKey = degradationProperty.getName() + "=" + i;

            JsonObject variantObject = new JsonObject();
            variantObject.addProperty("model", modelId.toString());

            variants.add(variantKey, variantObject);
        }

        json.add("variants", variants);
    }

    @Override
    public JsonElement get() {
        return null;
    }
}
