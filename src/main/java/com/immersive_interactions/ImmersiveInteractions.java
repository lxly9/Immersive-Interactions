package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.mixin.BlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
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
		Registries.BLOCK.forEach(block -> {
			if (block instanceof Oxidizable) {
				BlockState defaultState = block.getDefaultState();

				if (defaultState.contains(WAXED)) {
					defaultState = defaultState.with(WAXED, false);
				}
				if (defaultState.contains(DEGRADATION)) {
					defaultState = defaultState.with(DEGRADATION, 0);
				}

				((BlockAccessor) block).callSetDefaultState(defaultState);
			}
		});
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	private boolean shouldInject(Class<?> clazz) {
		String className = clazz.getSimpleName().toLowerCase();
		return className.contains("oxidizable") && !className.equals("oxidizable");
	}
}
