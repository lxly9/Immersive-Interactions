package com.gayasslily.immersive_interactions;

import com.gayasslily.immersive_interactions.util.ModItemTooltips;
import net.fabricmc.api.ClientModInitializer;

public class ImmersiveInteractionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModItemTooltips.register();
	}
}