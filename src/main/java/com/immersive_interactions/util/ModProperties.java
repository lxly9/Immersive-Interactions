package com.immersive_interactions.util;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;

public class ModProperties {
    public static final BooleanProperty WAXED = BooleanProperty.of("waxed");
    public static final IntProperty DEGRADATION = IntProperty.of("degradation", 0, 3);

    public static final BooleanProperty CRACKED = BooleanProperty.of("cracked");
    public static final BooleanProperty MOSSY = BooleanProperty.of("mossy");
    public static final EnumProperty<Color> COLOR = EnumProperty.of("color", Color.class);
}
