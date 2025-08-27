package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ImmersiveInteractions implements ModInitializer {
	public static final String MOD_ID = "immersive_interactions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isModLoaded;

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Immersive Interactions");
		ModItems.registerModItems();
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

	public static boolean hasVariant(String path, String prefix, boolean forward) {
		if (path == null || path.isEmpty()) return false;

		Identifier originalId = Identifier.tryParse(path);
		if (originalId == null) return false;

		String basePath = originalId.getPath();

		String targetPath;
		if (forward) {
			if (prefix.equals("chiseled_") && path.contains("copper_block")) {
				targetPath = "chiseled_copper";
			} else {
				targetPath = prefix + basePath;
			}
		} else {
			if (!basePath.startsWith(prefix)) return false;
			targetPath = basePath.substring(prefix.length());

			if (prefix.equals("chiseled_") && basePath.startsWith("chiseled_copper")) {
				targetPath = "copper_block";
			}
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) {
				return true;
			}
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

	public static Text joinWithAnd(List<Text> variants) {
		if (variants.isEmpty()) {
			return Text.empty();
		}

		if (variants.size() == 1) {
			return variants.get(0);
		}

		if (variants.size() == 2) {
			return Text.empty()
					.append(variants.get(0))
					.append(" ")
					.append(Text.translatable("tooltip.immersive_interactions.and"))
					.append(" ")
					.append(variants.get(1));
		}

		MutableText result = Text.empty();
		for (int i = 0; i < variants.size(); i++) {
			if (i > 0) {
				if (i == variants.size() - 1) {
					result.append(" ").append(Text.translatable("tooltip.immersive_interactions.and")).append(" ");
				} else {
					result.append(", ");
				}
			}
			result.append(variants.get(i));
		}
		return result;
	}

	public static Block getBlockVariant(String prefix, String path) {
		if (path == null || path.isEmpty()) return null;

		Identifier originalId = Identifier.tryParse(path);
		if (originalId == null) return null;

		String targetPath;
		if (originalId.getPath().contains(prefix)) {
			targetPath = originalId.getPath().substring(prefix.length());
		} else if (prefix.equals("chiseled_") && path.contains("copper_block")) {
			targetPath = "copper";
		} else {
			targetPath = originalId.getPath();
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) {
				return Registries.BLOCK.get(id);
			}
		}

		return null;
	}
}