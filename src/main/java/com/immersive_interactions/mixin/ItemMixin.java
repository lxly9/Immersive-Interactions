package com.immersive_interactions.mixin;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.immersive_interactions.datagen.ModItemTagProvider;
import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.item.custom.ChiselItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

import static com.immersive_interactions.util.BlockTransformationHelper.copyProperty;
import static com.immersive_interactions.util.BlockTransformationHelper.findBestMatch;
import static com.immersive_interactions.util.DyeMatcher.dyedBlockMatcher;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow @Final private static Logger LOGGER;

    @WrapMethod(method = "useOnBlock")
    private ActionResult mod_id$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        String blockIdString = Registries.BLOCK.getId(block).toString();

        if (!world.isClient){
            if (itemStack.getItem() instanceof PickaxeItem && state.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS)) {
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
                return ActionResult.success(state.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS));
            }
            if (state.isIn(ModBlockTagProvider.CRACKED_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK)) {
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
                return ActionResult.success(state.isIn(ModBlockTagProvider.CRACKED_BLOCKS));
            }
            if (itemStack.getItem() instanceof ShearsItem && state.isIn(ModBlockTagProvider.MOSSY_BLOCKS)) {
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
                return ActionResult.success(state.isIn(ModBlockTagProvider.MOSSY_BLOCKS));
            }
            if (state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_MOSS)) {
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
                return ActionResult.success(state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS));
            }
            if (state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
                if (state.getBlock() instanceof PistonBlock) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS);
                    if (newBlock != null) {
                        BlockState newState = newBlock.getDefaultState();

                        // Only copy the facing property and avoid copying the extended state
                        for (Property<?> property : state.getProperties()) {
                            // Only copy the facing property, and avoid the extended property
                            if (property == PistonBlock.FACING && newState.contains(property)) {
                                newState = copyProperty(newState, state, property);
                            }
                        }

                        world.setBlockState(pos, newState);
                        world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrement(1);
                    }
                    return ActionResult.success(state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS));
                } else {
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
            if (state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_AMETHYST)) {
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
                return ActionResult.success(state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS));
            }
            if (itemStack.getItem() instanceof ChiselItem) {
                if (state.isIn(ModBlockTagProvider.CHISELABLE_BLOCKS)) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.CHISELED_BLOCKS);
                    if (newBlock != null) {
                        BlockState newState = newBlock.getDefaultState();
                        for (Property<?> property : state.getProperties()) {
                            if (newState.contains(property)) {
                                newState = copyProperty(newState, state, property);
                            }
                        }

                        world.setBlockState(pos, newState);
                        world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                        context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(),
                                item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    }
                    return ActionResult.success(state.isIn(ModBlockTagProvider.CHISELABLE_BLOCKS));

                } else if (state.isIn(ModBlockTagProvider.CHISELED_BLOCKS)) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.CHISELABLE_BLOCKS);
                    if (newBlock != null) {
                        BlockState newState = newBlock.getDefaultState();
                        for (Property<?> property : state.getProperties()) {
                            if (newState.contains(property)) {
                                newState = copyProperty(newState, state, property);
                            }
                        }

                        world.setBlockState(pos, newState);
                        world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                        context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(),
                                item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    }
                    return ActionResult.success(state.isIn(ModBlockTagProvider.CHISELED_BLOCKS));
                }
            }
        }
        return original.call(context);
    }
}
