package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.mixin.BlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.immersive_interactions.util.ModProperties.*;


public class ImmersiveInteractions implements ModInitializer {
	public static final String MOD_ID = "immersive_interactions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isModLoaded;

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Immersive Interactions");
		ModItems.registerModItems();
		applyBlockStates();
//		registerChunkReplacement();
//		registerPlacementInterception();

	}

	private void applyBlockStates() {
		Registries.BLOCK.forEach(block -> {
			if (isOxidizable(block.getClass())) {
				BlockState defaultState = block.getDefaultState();
				if (block.getName().toString().contains(".*(exposed_|weathered_|oxidized_)*.")){
					return;
				}

				if (defaultState.contains(WAXED) && defaultState.contains(DEGRADATION)) {
					defaultState = defaultState.with(WAXED, false).with(DEGRADATION, 0);
				}

				((BlockAccessor) block).callSetDefaultState(defaultState);
			}
		});
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isOxidizable(Class<?> clazz) {
		String className = clazz.getSimpleName().toLowerCase();
		return className.contains("oxidizable") && !className.equals("oxidizable");
	}

	public static boolean isInstanceOf(Object obj, String className) {
		try {
			Class<?> clazz = Class.forName(className);
			return clazz.isInstance(obj);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}