package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.mixin.BlockAccessor;
import com.immersive_interactions.util.CopperBlockStateReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

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
		Path outputDir = FabricLoader.getInstance().getGameDir().resolve("generated_blockstates");
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
				new CopperBlockStateReloadListener(outputDir)
		);
	}

	private void applyBlockStates() {
		Registries.BLOCK.forEach(block -> {
			if (isOxidizable(block.getClass())) {
				BlockState defaultState = block.getDefaultState();

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