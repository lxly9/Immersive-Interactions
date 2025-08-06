package com.immersive_interactions.util;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import org.spongepowered.asm.mixin.Unique;

public class ModProperties {
    public static final BooleanProperty WAXED = BooleanProperty.of("waxed");
    public static final IntProperty DEGRADATION = IntProperty.of("degradation", 0, 3);
}
