package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.util.DisableRecipes;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersiveInteractions implements ModInitializer {
	public static final String MOD_ID = "immersive_interactions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Immersive Interactions");
		ModItems.registerModItems();
	}
}
