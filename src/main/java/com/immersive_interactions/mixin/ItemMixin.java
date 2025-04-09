package com.immersive_interactions.mixin;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.immersive_interactions.datagen.ModItemTagProvider;
import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.item.custom.ChiselItem;
import com.immersive_interactions.item.custom.PatinaItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static com.immersive_interactions.ImmersiveInteractions.isModLoaded;
import static com.immersive_interactions.util.BlockTransformationHelper.copyProperty;
import static com.immersive_interactions.util.BlockTransformationHelper.findBestMatch;
import static com.immersive_interactions.util.DyeMatcher.dyedBlockMatcher;
import static com.immersive_interactions.util.WoodTransformationHelper.transformLogToWood;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow @Final private static Logger LOGGER;

    @WrapMethod(method = "useOnBlock")
    private ActionResult immersive_interactions$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        String blockIdString = Registries.BLOCK.getId(block).toString();
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);

        if (!world.isClient){
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
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
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
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
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
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
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
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
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS));
            }
            if (state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
                if (state.getBlock() instanceof PistonBlock) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS);
                    if (newBlock != null) {
                        BlockState newState = newBlock.getDefaultState();

                        for (Property<?> property : state.getProperties()) {
                            if (property == PistonBlock.FACING && newState.contains(property)) {
                                newState = copyProperty(newState, state, property);
                            }
                        }

                        world.setBlockState(pos, newState);
                        world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                    }
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
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
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
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS));
            }
            if (state.isIn(BlockTags.LOGS) && (itemStack.isIn(ModItemTagProvider.CAN_APPLY_BARK) || itemStack.isOf(barkFD))) {
                    Block newBlock = transformLogToWood(blockIdString);
                    String clickedWood = block.toString();
                    if (newBlock != null) {
                        BlockState newState = newBlock.getDefaultState();
                        for (Property<?> property : state.getProperties()) {
                            if (newState.contains(property)) {
                                newState = copyProperty(newState, state, property);
                            }
                        }

                        world.setBlockState(pos, newState);
                        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(clickedWood.contains("stripped") || clickedWood.contains("log"));
            }
            if ((state.isIn(ConventionalBlockTags.DYED) || state.isIn(ConventionalBlockTags.GLASS_BLOCKS) || state.isIn(ConventionalBlockTags.GLASS_PANES)) && itemStack.getItem() instanceof DyeItem) {
                Block newBlock = dyedBlockMatcher(blockIdString, itemStack);
                BlockState newState = newBlock.getDefaultState();
                String dyeColor = itemStack.getItem().toString().replace("dye", "");

                for (Property<?> property : state.getProperties()) {
                    if (newState.contains(property)) {
                        newState = copyProperty(newState, state, property);
                    }
                }
                if (!state.toString().contains(dyeColor) || !(state.getBlock() instanceof BannerBlock) || !(state.getBlock() instanceof BedBlock)) {
                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS);
                    context.getStack().decrementUnlessCreative(1, player);
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.success(state.isIn(ConventionalBlockTags.DYED));
                }
            }
            if (itemStack.getItem() instanceof ChiselItem) {
                if (state.isIn(ModBlockTagProvider.CHISELABLE_BLOCKS) )  {
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
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
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
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.success(state.isIn(ModBlockTagProvider.CHISELED_BLOCKS));
                }
            }
        }
        return original.call(context);
    }

    @Inject(at=@At("HEAD"),method = "appendTooltip")
    private void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);

        if (stack.isIn(ModItemTagProvider.CAN_APPLY_MOSS)) {
            tooltip.add(Text.translatable("tag.item.immersive_interactions.can_apply_moss").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.isIn(ModItemTagProvider.CAN_APPLY_BARK) || stack.isOf(barkFD)) {
            tooltip.add(Text.translatable("tag.item.immersive_interactions.can_apply_bark").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
            tooltip.add(Text.translatable("tag.item.immersive_interactions.can_apply_slime").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.isIn(ModItemTagProvider.CAN_APPLY_AMETHYST)) {
            tooltip.add(Text.translatable("tag.item.immersive_interactions.can_apply_amethyst").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK)) {
            tooltip.add(Text.translatable("tag.item.immersive_interactions.can_repair_brick").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.isIn(ModItemTagProvider.CAN_WAX_COPPER)) {
            if (!isModLoaded("waxed_workstations")) {
                tooltip.add(Text.translatable("tag.item.immersive_interactions.can_wax_copper").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            else {
                tooltip.add(Text.translatable("tag.item.immersive_interactions.can_wax_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
        }
        if (stack.getItem() instanceof PatinaItem) {
            tooltip.add(Text.translatable("tooltip.item.immersive_interactions.copper_patina").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.getItem() instanceof ChiselItem) {
            tooltip.add(Text.translatable("tooltip.item.immersive_interactions.chisel").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.getItem() instanceof ShearsItem) {
            tooltip.add(Text.translatable("tooltip.item.immersive_interactions.shears").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }
        if (stack.getItem() instanceof DyeItem) {
            tooltip.add(Text.translatable("tooltip.item.immersive_interactions.dye").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
        }

        if (stack.getItem() instanceof BlockItem) {
            String itemString = stack.getItem().toString();
            Block blockItem = Registries.BLOCK.get(Identifier.of(itemString));
            BlockState blockState = blockItem.getDefaultState();

            if (blockState.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.mossable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.crackable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.slimable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.amethystable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.CHISELABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.chiselable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (isModLoaded("waxed_workstations") && blockState.isIn(ConventionalBlockTags.VILLAGER_JOB_SITES)) {
                    tooltip.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (itemString.contains("copper") && !(itemString.contains("ore") || itemString.contains("piston") || itemString.contains("raw"))) {
                    tooltip.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
        }
    }
}
