package com.immersive_interactions.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import static com.immersive_interactions.ImmersiveInteractions.*;

public class ModRenderLayers {
    public static final Identifier WAX_OVERLAY_TEXTURE = Identifier.of(MOD_ID, "textures/misc/wax_overlay.png");

    public static final RenderLayer WAX_OVERLAY = RenderLayer.of(
        "wax_overlay",
        VertexFormats.POSITION_TEXTURE_COLOR,
        VertexFormat.DrawMode.QUADS,
        256,
        RenderLayer.MultiPhaseParameters.builder()
            .texture(new RenderPhase.Texture(WAX_OVERLAY_TEXTURE, false, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
            .cull(RenderPhase.DISABLE_CULLING)
            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false)
    );
}
