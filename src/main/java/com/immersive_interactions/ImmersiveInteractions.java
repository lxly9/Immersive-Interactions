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
		if (path == null || path.isEmpty()) return false;
		if (!path.contains("cracked_")) {
			String crackedName;
			crackedName = "cracked_" + path;
			if (path.contains("copper")) crackedName = path.replace("copper", "cracked_copper");
			return getBlockByName(crackedName) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasUncrackedVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("cracked_")) {
			String basePath = path.replace("cracked_", "");
			return getBlockByName(basePath) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasMossyVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (!path.contains("mossy_")) {
			String mossyName;
			mossyName = "mossy_" + path;
			if (path.contains("copper")) mossyName = path.replace("copper", "mossy_copper");
			return getBlockByName(mossyName) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasUnmossedVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("mossy_")) {
			String basePath = path.replace("mossy_", "");
			return getBlockByName(basePath) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasChiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (!path.contains("chiseled_")) {
			String chiseledName;
			chiseledName = "chiseled_" + path;
			if (path.contains("copper")) chiseledName = path.replace("copper", "chiseled_copper");
			return getBlockByName(chiseledName) != Blocks.AIR;
		}
        return false;
    }


	public static boolean hasUnchiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("chiseled_")) {
			String basePath = path.replace("chiseled_", "");
			return getBlockByName(basePath) != Blocks.AIR;
		}
        return false;
    }


	public static boolean isInstanceOf(Object obj, String className) {
		try {
			return Class.forName(className).isInstance(obj);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Text joinWithAnd(List<Text> variants) {
		if (variants.isEmpty()) {
			return Text.empty();
		}

		if (variants.size() == 1) {
			return variants.getFirst();
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
			if (id.getPath().equals(name)) return block;
			if (id.getPath().equals(name + "_block")) return block;
			if (name.endsWith("_block")) {
				if (id.getPath().equals(name.replace("_block", ""))) return block;
			}
		}
		return Blocks.AIR;
	}

	public static EntityType<?> getMinecartByName(String name) {
		for (EntityType<?> type : Registries.ENTITY_TYPE) {
			Identifier id = Registries.ENTITY_TYPE.getId(type);
			if (id.getPath().equals(name)) {
				return type;
			}
		}
		return null;
	}

	public static EntityType<?> getBoatEntityByName(String name) {
		for (EntityType<?> type : Registries.ENTITY_TYPE) {
			Identifier id = Registries.ENTITY_TYPE.getId(type);
			if (id.getPath().equals(name)) {
				return type;
			}
		}
		return null;
	}

	public static Item getItemByName(String name) {
		for (Item item : Registries.ITEM) {
			Identifier id = Registries.ITEM.getId(item);
			if (id.getPath().equals(name)) {
				return item;
			}
		}
		return Items.AIR;
	}
}