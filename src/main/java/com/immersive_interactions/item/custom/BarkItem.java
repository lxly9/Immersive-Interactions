package com.immersive_interactions.item.custom;

import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;

import static com.immersive_interactions.ImmersiveInteractions.isModLoaded;

public class BarkItem extends Item {
    public BarkItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return !isModLoaded("farmersdelight");
    }
}
