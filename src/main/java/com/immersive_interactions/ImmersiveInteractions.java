package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImmersiveInteractions implements ModInitializer {
	public static final String MOD_ID = "immersive_interactions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isModLoaded;

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Immersive Interactions");
		ModItems.registerModItems();
		applyBlockStates();
	}

	private void applyBlockStates() {
		Registries.BLOCK.forEach(block -> {
			Identifier blockId = Registries.BLOCK.getId(block);
			String path = blockId.getPath();
			BlockState defaultState = block.getDefaultState();
		});
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isOxidizable(Class<?> clazz) {
		String className = clazz.getSimpleName().toLowerCase();
		return className.contains("oxidizable");
	}

	public static boolean hasCrackedVariant(String path) {
		if (!path.startsWith("cracked_")) {
			Identifier crackedId = Identifier.of("cracked_" + path);
			return Registries.BLOCK.containsId(crackedId);
		}
		return false;
	}

	public static boolean hasUncrackedVariant(String path) {
		if (path.startsWith("cracked_")) {
			String uncrackedPath = path.substring("cracked_".length());
			Identifier uncrackedId = Identifier.of(uncrackedPath);
			return Registries.BLOCK.containsId(uncrackedId);
		}
		return false;
	}

	public static boolean hasMossyVariant(String path) {
		if (!path.startsWith("mossy_")) {
			Identifier mossyId = Identifier.of("mossy_" + path);
			return Registries.BLOCK.containsId(mossyId);
		}
		return false;
	}

	public static boolean hasUnmossedVariant(String path) {
		if (path.startsWith("mossy_")) {
			String unmossedPath = path.substring("mossy_".length());
			Identifier unmossedId = Identifier.of(unmossedPath);
			return Registries.BLOCK.containsId(unmossedId);
		}
		return false;
	}

	public static boolean hasChiseledVariant(String path) {
		if (!path.startsWith("chiseled_")) {
			if (path.contains("copper_block")) {
				Identifier chiseledId = Identifier.of("chiseled_" + path.replace("_block", ""));
				return Registries.BLOCK.containsId(chiseledId);
			}
			Identifier chiseledId = Identifier.of("chiseled_" + path);
			return Registries.BLOCK.containsId(chiseledId);
		}
		return false;
	}

	public static boolean hasUnchiseledVariant(String path) {
		if (path.startsWith("chiseled_")) {
			if (path.contains("chiseled_copper")) {
				String unchiseledPath = path.substring("chiseled_".length());
				Identifier unchiseledId = Identifier.of(unchiseledPath + "_block");
				return Registries.BLOCK.containsId(unchiseledId);
			}
			String unchiseledPath = path.substring("chiseled_".length());
			Identifier unchiseledId = Identifier.of(unchiseledPath);
			return Registries.BLOCK.containsId(unchiseledId);
		}
		return false;
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