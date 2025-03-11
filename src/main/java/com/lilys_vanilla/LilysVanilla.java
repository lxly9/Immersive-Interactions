package com.lilys_vanilla;

import com.lilys_vanilla.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LilysVanilla implements ModInitializer {
	public static final String MOD_ID = "lilys_vanilla";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Lily's Vanilla");
		ModItems.registerModItems();
	}
}