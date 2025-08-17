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
import net.minecraft.util.Identifier;
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
//		registerChunkReplacement();
//		Path outputDir = FabricLoader.getInstance().getGameDir().resolve("generated_blockstates");
//		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
//				new CopperBlockStateReloadListener(outputDir)
//		);
	}

	private void applyBlockStates() {
		Registries.BLOCK.forEach(block -> {
			Identifier blockId = Registries.BLOCK.getId(block);
			String path = blockId.getPath();
			BlockState defaultState = block.getDefaultState();

			if (isOxidizable(block.getClass())) {

				if (defaultState.contains(WAXED) && defaultState.contains(DEGRADATION)) {
					defaultState = defaultState.with(WAXED, false).with(DEGRADATION, 0);
				}

				((BlockAccessor) block).callSetDefaultState(defaultState);
			}

//			if (hasCrackedVariant(path, blockId)); {
//
//				if (defaultState.contains(CRACKED)) {
//					defaultState = defaultState.with(CRACKED, false);
//				}
//
//				((BlockAccessor) block).callSetDefaultState(defaultState);
//
//			}
		});
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isOxidizable(Class<?> clazz) {
		String className = clazz.getSimpleName().toLowerCase();
		return className.contains("oxidizable") && !className.equals("oxidizable");
	}

	public static boolean hasCrackedVariant(String path, Identifier blockId) {
		if (!path.startsWith("cracked_")) {
			Identifier crackedId = Identifier.of(blockId.getNamespace(), "cracked_" + path);
			return Registries.BLOCK.containsId(crackedId);
		}
		return false;
	}

	public static boolean hasUncrackedVariant(String path, Identifier blockId) {
		if (path.startsWith("cracked_")) {
			String uncrackedPath = path.substring("cracked_".length());
			Identifier uncrackedId = Identifier.of(blockId.getNamespace(), uncrackedPath);
			return Registries.BLOCK.containsId(uncrackedId);
		}
		return false;
	}

	public static boolean hasMossyVariant(String path, Identifier blockId) {
		if (!path.startsWith("mossy_")) {
			Identifier mossyId = Identifier.of(blockId.getNamespace(), "mossy_" + path);
			return Registries.BLOCK.containsId(mossyId);
		}
		return false;
	}

	public static boolean hasUnmossedVariant(String path, Identifier blockId) {
		if (path.startsWith("mossy_")) {
			String unmossedPath = path.substring("mossy_".length());
			Identifier unmossedId = Identifier.of(blockId.getNamespace(), unmossedPath);
			return Registries.BLOCK.containsId(unmossedId);
		}
		return false;
	}

	public static boolean hasChiseledVariant(String path, Identifier blockId) {
		if (!path.startsWith("chiseled_")) {
			if (path.contains("copper_block")) {
				Identifier chiseledId = Identifier.of(blockId.getNamespace(), "chiseled_" + path.replace("_block", ""));
				return Registries.BLOCK.containsId(chiseledId);
			}
			Identifier chiseledId = Identifier.of(blockId.getNamespace(), "chiseled_" + path);
			return Registries.BLOCK.containsId(chiseledId);
		}
		return false;
	}

	public static boolean hasUnchiseledVariant(String path, Identifier blockId) {
		if (path.startsWith("chiseled_")) {
			if (path.contains("chiseled_copper")) {
				String unchiseledPath = path.substring("chiseled_".length());
				Identifier unchiseledId = Identifier.of(blockId.getNamespace(), unchiseledPath + "_block");
				return Registries.BLOCK.containsId(unchiseledId);
			}
			String unchiseledPath = path.substring("chiseled_".length());
			Identifier unchiseledId = Identifier.of(blockId.getNamespace(), unchiseledPath);
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

//	@Unique
//	public BlockState getCopper(BlockState state, Block block) {
//		var id = Registries.BLOCK.getId(block);
//		String path = id.getPath();
//
//		if (path.contains("copper")) {
//			boolean waxed = path.contains("waxed");
//
//			int degradation = 0;
//			if (path.contains("exposed")) degradation = 1;
//			else if (path.contains("weathered")) degradation = 2;
//			else if (path.contains("oxidized")) degradation = 3;
//
//			String basePath = path
//					.replace("waxed_", "")
//					.replace("exposed_", "")
//					.replace("weathered_", "")
//					.replace("oxidized_", "");
//
//			Block baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), basePath));
//
//			if (baseBlock == Blocks.AIR) {
//				String altBasePath = basePath + "_block";
//				baseBlock = Registries.BLOCK.get(Identifier.of(id.getNamespace(), altBasePath));
//			}
//
//			if (baseBlock != Blocks.AIR) {
//				return baseBlock.getStateWithProperties(state).with(WAXED, waxed).with(DEGRADATION, degradation);
//			}
//
//		}
//		return state;
//	}
//
//	private void registerChunkReplacement() {
//		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
//			if (!world.isClient) {
//				replaceCopperInChunk(chunk);
//			}
//		});
//	}
//
//	private void replaceCopperInChunk(WorldChunk chunk) {
//		BlockPos.Mutable pos = new BlockPos.Mutable();
//		for (int x = 0; x < 16; x++) {
//			for (int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
//				for (int z = 0; z < 16; z++) {
//					pos.set(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z);
//					BlockState state = chunk.getBlockState(pos);
//					if (isOxidizable(state.getBlock().getClass())) {
//						BlockState replacement = getCopper(state, state.getBlock());
//						String newCopper = replacement.toString();
//						if (!newCopper.matches(".*(ore|raw).*")) {
//							chunk.getWorld().setBlockState(pos, replacement, 3);
//						}
//					}
//				}
//			}
//		}
//	}
}