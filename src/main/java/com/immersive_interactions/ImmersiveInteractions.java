package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.mixin.BlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OxidizableBlock;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.immersive_interactions.util.ModProperties.*;


public class ImmersiveInteractions implements ModInitializer {
	public static final String MOD_ID = "immersive_interactions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isModLoaded;

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Immersive Interactions");
		ModItems.registerModItems();
		setDefaultStatesAfterRegistration();
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public void setDefaultStatesAfterRegistration() {
		for (Block block : Registries.BLOCK) {
			if (block instanceof OxidizableBlock) {
				BlockState defaultState = block.getDefaultState();
				if (!defaultState.getProperties().contains(DEGRADATION)) continue;

				BlockState newDefault = defaultState.with(DEGRADATION, 0).with(WAXED, false);
				((BlockAccessor) block).callSetDefaultState(newDefault);
			}
		}
	}
}
