package com.immersive_interactions.mixin;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.immersive_interactions.datagen.ModItemTagProvider;
import com.immersive_interactions.item.ModItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(Item.class)
public class ItemMixin {
    @WrapMethod(method = "useOnBlock")
    private ActionResult mod_id$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        String blockIdString = Registries.BLOCK.getId(block).toString();

        if (itemStack.getItem() instanceof PickaxeItem) {
            if (!world.isClient && state.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS)) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.CRACKED_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_DEEPSLATE_BRICKS_HIT, SoundCategory.BLOCKS);
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(),
                            item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS));
        }
        if (state.isIn(ModBlockTagProvider.CRACKED_BLOCKS)&& itemStack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK)) {
            if (!world.isClient) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.CRACKABLE_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_MUD_STEP, SoundCategory.BLOCKS);
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.CRACKED_BLOCKS));
        }
        if (itemStack.getItem() instanceof ShearsItem) {
            if (!world.isClient && state.isIn(ModBlockTagProvider.MOSSY_BLOCKS)) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.MOSSABLE_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS);
                    Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(),
                            item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.MOSSY_BLOCKS));
        }
        if (state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS)&& itemStack.isIn(ModItemTagProvider.CAN_APPLY_MOSS)) {
            if (!world.isClient) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.MOSSY_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_MOSS_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS));
        }
        if (state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS)&& itemStack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
            if (!world.isClient) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS));
        }
        if (state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS)&& itemStack.isIn(ModItemTagProvider.CAN_APPLY_AMETHYST)) {
            if (!world.isClient) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.AMETHYST_BLOCKS);
                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS));
        }
        return original.call(context);
    }

    @Unique
    private Block findBestMatch(String blockIdString, TagKey<Block> tagKey) {
        LevenshteinDistance distance = new LevenshteinDistance();
        Block bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        RegistryEntryList<Block> blocksInTag = Registries.BLOCK.getEntryList(tagKey).orElse(null);
        if (blocksInTag == null) return null;

        for (RegistryEntry<Block> entry : blocksInTag) {
            Block tagBlock = entry.value();
            String tagBlockId = Registries.BLOCK.getId(tagBlock).toString();
            int currentDistance = distance.apply(blockIdString, tagBlockId);

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                bestMatch = tagBlock;
            }
        }
        return bestMatch;
    }

    @Unique
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }
}
