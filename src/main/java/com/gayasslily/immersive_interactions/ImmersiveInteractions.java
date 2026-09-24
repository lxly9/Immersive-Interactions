package com.gayasslily.immersive_interactions;

import com.gayasslily.immersive_interactions.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
	}

	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean hasUncrackedVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("cracked_")) {
			String basePath = path.replace("cracked_", "");
			return getBlockByName(basePath) != Blocks.AIR;
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

	public static boolean hasUnchiseledVariant(String path) {
		if (path == null || path.isEmpty()) return false;
		if (path.contains("chiseled_") && !path.contains("chiseled_bookshelf")) {
			String basePath = path.replace("chiseled_", "");
			return getBlockByName(basePath) != Blocks.AIR;
		}
        return false;
    }

	public static Block getBlockByName(String name) {
		for (Block block : Registries.BLOCK) {
			Identifier id = Registries.BLOCK.getId(block);
//			if (CLUTTERNOMORE) if (id.getNamespace().equals("clutternomore") && id.getPath().equals(name)) return block;
			if (id.getPath().equals(name)) return block;
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