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
		buildDynamicCopperMap();
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

	private static final Map<Block, BlockState> COPPER_MAP = new HashMap<>();

	private void buildDynamicCopperMap() {
		Registries.BLOCK.forEach(block -> {
			var id = Registries.BLOCK.getId(block);
			String path = id.getPath();

			if (!path.contains("copper")) return;

			boolean waxed = path.contains("waxed");

			int degradation = 0;
			if (path.contains("exposed")) degradation = 1;
			else if (path.contains("weathered")) degradation = 2;
			else if (path.contains("oxidized")) degradation = 3;

			String basePath = path
					.replace("waxed_", "")
					.replace("exposed_", "")
					.replace("weathered_", "")
					.replace("oxidized_", "");

			Block baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), basePath));

			if (baseBlock == Blocks.AIR) {
				String altBasePath = basePath + "_block";
				baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), altBasePath));
			}

			if (baseBlock != Blocks.AIR) {
				try {
					BlockState replacement = baseBlock.getDefaultState().with(WAXED, waxed).with(DEGRADATION, degradation);
					COPPER_MAP.put(block, replacement);
				} catch (IllegalArgumentException ignored) {
				}
			}
		});
	}
}