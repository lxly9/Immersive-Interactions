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
    public static boolean CLUTTERNOMORE = isModLoaded("clutternomore");

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

	public static boolean hasCrackedVariant(String identifier) {
        if (identifier == null || identifier.isEmpty()) return false;
        String[] path = identifier.split(":");
        if (!path[1].contains("cracked_")) {
            if (path[1].equals("copper_block")) identifier = identifier.replace("_block", "");
            return getBlockByName("cracked_", identifier) != Blocks.AIR;
        }
        return false;
	}

	public static boolean hasUncrackedVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("cracked_")) {
			String basePath = path.replace("cracked_", "");
			return getBlockByName("", basePath) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasMossyVariant(String identifier) {
		if (identifier == null || identifier.isEmpty()) return false;
        String[] path = identifier.split(":");
		if (!path[1].contains("mossy_")) {
			if (path[1].equals("copper_block")) identifier = identifier.replace("_block", "");
			return getBlockByName("mossy_", identifier) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasUnmossedVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("mossy_")) {
			String basePath = path.replace("mossy_", "");
			return getBlockByName("", basePath) != Blocks.AIR;
		}
		return false;
	}

	public static boolean hasChiseledVariant(String identifier) {
        if (identifier == null || identifier.isEmpty()) return false;
        String[] path = identifier.split(":");
        if (!path[1].contains("chiseled_")) {
            if (path[1].contains("copper")) {
                String copperId = identifier.replace("copper", "chiseled_copper");
                return getBlockByName("", copperId) != Blocks.AIR;
            }
            return getBlockByName("chiseled_", identifier) != Blocks.AIR;
        }
        return false;
    }


	public static boolean hasUnchiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("chiseled_") && !path.contains("chiseled_bookshelf")) {
			String basePath = path.replace("chiseled_", "");
			return getBlockByName("", basePath) != Blocks.AIR;
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

	public static Block getBlockVariant(String prefix, String identifier) {
		if (identifier == null || identifier.isEmpty()) return null;
        String[] path = identifier.split(":");
        String targetPath;

		if (path[1].contains(prefix)) {
			targetPath = path[1].replace(prefix, "");
		} else if (prefix.equals("chiseled_") && path[1].contains("copper_block")) {
			targetPath = "copper";
		} else if (path[1].equals("chiseled_copper")) {
			targetPath = "copper_block";
		} else {
			targetPath = path[1];
		}

		for (Identifier id : Registries.BLOCK.getIds()) {
            if (CLUTTERNOMORE && path[1].contains("/")) {
                String[] path2 = path[1].split("/");
                targetPath = path2[1].replace(prefix, "");
                if (targetPath.contains("vertical_")) targetPath = targetPath.replace("vertical_", "");
                else if (targetPath.contains("_step")) targetPath = targetPath.replace("_step", "_stairs");
            }
            if (id.getPath().equals(targetPath)) return Registries.BLOCK.get(id);
        }

		return Blocks.AIR;
	}

	public static Block getBlockByName(String prefix, String name) {
		for (Block block : Registries.BLOCK) {
			Identifier id = Registries.BLOCK.getId(block);
            String[] path = name.split(":");
            String blockId = path[1];
            if (CLUTTERNOMORE && path[0].contains("clutternomore")) {
                if (path[1].contains("/")){
                    String[] clutter = path[1].split("/");
                    blockId = clutter[1];
                }
            }
            if (!prefix.isEmpty()) {
                if (path[1].contains("vertical")) blockId = "vertical_" + prefix + blockId.replace("vertical_", "");
                else blockId = prefix + blockId;
            }
            if (path[0].contains("clutternomore")) {
                if (id.toString().contains(blockId)) return block;
            }
            if (id.getPath().equals(blockId)) return block;
            if (id.getPath().equals(blockId + "_block")) return block;
            if (blockId.endsWith("_block")) {
                if (id.getPath().equals(blockId.replace("_block", ""))) return block;
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