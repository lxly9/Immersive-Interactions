package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

	public static boolean hasCrackedVariant(String path) {

		String targetPath = "cracked_" + path;

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasUncrackedVariant(String path) {
		if (path == null || path.isEmpty()) return false;

		String targetPath;
		if (path.contains("cracked_")) {
			targetPath = path.replace("cracked_", "");
			for (Identifier id : Registries.BLOCK.getIds()) {
				if (id.getPath().equals(targetPath)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasMossyVariant(String path) {

		String targetPath = "mossy_" + path;

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasUnmossedVariant(String path) {
		if (path == null || path.isEmpty()) return false;

		String targetPath;
		if (path.contains("mossy_")) {
			targetPath = path.replace("mossy_", "");
			for (Identifier id : Registries.BLOCK.getIds()) {
				if (id.getPath().equals(targetPath)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasChiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;

		if (path.contains("chiseled_")) return false;

		String targetPath;
		if (path.contains("copper_block")) {
			targetPath = path.replace("copper_block", "chiseled_copper");
		} else if (path.matches(".*(exposed_|weathered_|oxidized_).*")){
			targetPath = path.replace("_copper","_chiseled_copper");
		} else {
			targetPath = "chiseled_" + path;
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) return true;
		}
		return false;
	}

	public static boolean hasUnchiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;

		if (!path.contains("chiseled_")) return false;

		String basePath = path.replace("chiseled_","");

		String targetPath;
		if ((!path.matches(".*(exposed_|weathered_|oxidized_).*") && path.contains("copper"))) {
			targetPath = basePath + "_block";
		} else {
			targetPath = basePath;
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) return true;
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

		String targetPath;
		if (path.startsWith(prefix)) {
			targetPath = path.substring(prefix.length());
		} else if (prefix.equals("chiseled_") && path.contains("copper_block")) {
			targetPath = "copper";
		} else if (path.equals("chiseled_copper")) {
			targetPath = "copper_block";
		} else {
			targetPath = path;
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
			if (id.getPath().equals(targetPath)) {
				return Registries.BLOCK.get(id);
			}
		}

		return null;
	}

	public static Block getBlockByName(String name) {
		for (Block block : Registries.BLOCK) {
			Identifier id = Registries.BLOCK.getId(block);
			if (id.getPath().equals(name)) {
				return block;
			}
		}
		return Blocks.AIR;
	}

	public static EntityType<?> getMinecartByName(String name) {
		for (EntityType<?> type : Registries.ENTITY_TYPE) {
			Identifier id = Registries.ENTITY_TYPE.getId(type);
			if (id != null && id.getPath().equals(name)) {
				return type;
			}
		}
		return EntityType.MINECART;
	}

	public static Item getItemByName(String name) {
		for (Item item : Registries.ITEM) {
			Identifier id = Registries.ITEM.getId(item);
			if (id != null && id.getPath().equals(name)) {
				return item;
			}
		}
		return Items.AIR;
	}
}