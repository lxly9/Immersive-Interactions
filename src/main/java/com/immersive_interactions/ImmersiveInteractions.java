package com.immersive_interactions;

import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.mixin.BlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
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
		registerChunkReplacement();
		registerPlacementInterception();
		Registries.BLOCK.forEach(block -> {
			if (block instanceof Oxidizable) {
				BlockState defaultState = block.getDefaultState();

				if (defaultState.contains(WAXED) && defaultState.contains(DEGRADATION)) {
					defaultState = defaultState.with(WAXED, false).with(DEGRADATION, 0);
				}

				((BlockAccessor) block).callSetDefaultState(defaultState);
			}
			if (isModLoaded("copperrails") && isInstanceOf(block, "com.copperrails.block.OxidizableCopperRailBlock")) {
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

	private boolean shouldInject(Class<?> clazz) {
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
					BlockState replacement = baseBlock.getDefaultState()
							.with(WAXED, waxed)
							.with(DEGRADATION, degradation);
					COPPER_MAP.put(block, replacement);
				} catch (IllegalArgumentException e) {
					// Base block doesn’t have the properties — skip
				}
			}
		});
	}

	private void registerChunkReplacement() {
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			if (!world.isClient) {
				replaceCopperInChunk(chunk);
			}
		});
	}


	private void replaceCopperInChunk(WorldChunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int x = 0; x < 16; x++) {
			for (int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
				for (int z = 0; z < 16; z++) {
					pos.set(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z);
					BlockState state = chunk.getBlockState(pos);
					BlockState replacement = COPPER_MAP.get(state.getBlock());
					if (replacement != null) {
						chunk.setBlockState(pos, replacement, false);
					}
				}
			}
		}
	}

	private void registerPlacementInterception() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.getItem() instanceof BlockItem blockItem) {
				Block block = blockItem.getBlock();
				BlockState replacement = COPPER_MAP.get(block);
				if (replacement != null) {
					if (!world.isClient) {
						BlockPos placePos = hitResult.getBlockPos().offset(hitResult.getSide());
						world.setBlockState(placePos, replacement);
						if (!player.isCreative()) {
							stack.decrement(1);
						}
					}
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		});
	}
}
