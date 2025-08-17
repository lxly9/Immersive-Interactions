package com.immersive_interactions.mixin;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.immersive_interactions.datagen.ModItemTagProvider;
import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.item.custom.ChiselItem;
import com.immersive_interactions.item.custom.PatinaItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.immersive_interactions.ImmersiveInteractions.*;
import static com.immersive_interactions.util.BlockTransformationHelper.*;
import static com.immersive_interactions.util.DyeMatcher.dyedBlockMatcher;
import static com.immersive_interactions.util.ModProperties.*;
import static com.immersive_interactions.util.WoodTransformationHelper.transformLogToWood;

@Mixin(Item.class)
public abstract class ItemMixin implements ToggleableFeature {

    @Shadow
    private final RegistryEntry.Reference<Item> registryEntry = Registries.ITEM.createEntry((Item) (Object) this);

    @Unique
    public boolean isEnabled(FeatureSet enabledFeatures) {
        if ((Object) this instanceof BlockItem) {
            String key = registryEntry.getKey().get().toString();
            return !key.matches(".*(exposed_|weathered_|oxidized_|waxed_|cracked_|mossy_).*");
        }
        return true;
    }

    @WrapMethod(method = "useOnBlock")
    private ActionResult immersive_interactions$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        Identifier itemId = Registries.ITEM.getId(context.getStack().getItem());
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String path = blockId.getPath();
        String blockIdString = Registries.BLOCK.getId(block).toString();
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);

        if (!world.isClient){
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            if (itemStack.getItem() instanceof PickaxeItem && hasCrackedVariant(path, blockId)) {
                Block crackedId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), "cracked_" + path));

                world.setBlockState(pos, crackedId.getStateWithProperties(state));
                world.playSound(null, pos, SoundEvents.BLOCK_DEEPSLATE_BRICKS_HIT, SoundCategory.BLOCKS);
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }
            if (itemStack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK) && hasUncrackedVariant(path, blockId)) {
                Block unCrackedId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("cracked_", "")));

                world.setBlockState(pos, unCrackedId.getStateWithProperties(state));
                world.playSound(null, pos, SoundEvents.BLOCK_MUD_STEP, SoundCategory.BLOCKS);
                context.getStack().decrementUnlessCreative(1, player);
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }
            if (itemStack.isIn(ModItemTagProvider.CAN_APPLY_MOSS) && hasMossyVariant(path, blockId)) {
                Block mossyId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), "mossy_" + path));

                world.setBlockState(pos, mossyId.getStateWithProperties(state));
                world.playSound(null, pos, SoundEvents.BLOCK_MOSS_HIT, SoundCategory.BLOCKS);
                context.getStack().decrementUnlessCreative(1, player);
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }
            if (itemStack.getItem() instanceof ShearsItem && hasUnmossedVariant(path, blockId)) {
                Block unmossedID = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("mossy_", "")));

                world.setBlockState(pos, unmossedID.getStateWithProperties(state));
                world.playSound(null, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS);
                Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }
            if (itemStack.getItem() instanceof ChiselItem) {
                if (hasChiseledVariant(path, blockId))  {
                    if (path.contains("copper_block")) {
                        Block chiseledId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), "chiseled_" + path.replace("_block", "")));
                        world.setBlockState(pos, chiseledId.getStateWithProperties(state));
                    } else {
                        Block chiseledId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), "chiseled_" + path));

                        world.setBlockState(pos, chiseledId.getStateWithProperties(state));
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.SUCCESS;

                }
                if (hasUnchiseledVariant(path, blockId)) {
                    if (path.contains("chiseled_copper")) {
                        String unchiseledPath = path.substring("chiseled_".length());
                        Block unchiseledId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), unchiseledPath + "_block"));
                        world.setBlockState(pos, unchiseledId.getStateWithProperties(state));
                    } else {
                        Block unchiseledId = Registries.BLOCK.get(Identifier.of(blockId.getNamespace(), path.replace("chiseled_", "")));

                        world.setBlockState(pos, unchiseledId.getStateWithProperties(state));
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.SUCCESS;
                }
            }
            if (state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
                if (state.getBlock() instanceof PistonBlock) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS, world);

                    if (newBlock != null) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state));
                        world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                } else {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS, world);

                    if (newBlock != null) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state));
                        world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS));
            }
            if (state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_AMETHYST)) {
                    Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.AMETHYST_BLOCKS, world);

                    if (newBlock != null) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state));
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
                        world.setBlockState(pos, newBlock.getStateWithProperties(state));
                        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                    }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(clickedWood.contains("stripped") || clickedWood.contains("log"));
            }
            if (itemStack.getItem() instanceof DyeItem) {
                Block newBlock = dyedBlockMatcher(blockId, itemId);

                if (newBlock != Blocks.AIR && !newBlock.toString().matches(".*(bed|shulker|banner).*")) {
                    String dyeColor = itemStack.getItem().toString().replace("dye", "");

                    if (!state.toString().contains(dyeColor)) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state));
                        world.playSound(null, pos, SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS);
                        context.getStack().decrementUnlessCreative(1, player);
                        world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                        return ActionResult.SUCCESS;
                    }
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
            Block block = Registries.BLOCK.get(Identifier.of(itemString));
            Identifier blockId = Registries.BLOCK.getId(block);
            String path = blockId.getPath();
            BlockState blockState = block.getDefaultState();
            Optional<RegistryEntry<PointOfInterestType>> optional = PointOfInterestTypes.getTypeForState(blockState);

            if (hasMossyVariant(path, blockId)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.mossable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (hasCrackedVariant(path, blockId)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.crackable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (hasChiseledVariant(path, blockId)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.chiselable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.slimable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS)) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.amethystable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (isModLoaded("waxed_workstations") && optional.isPresent()) {
                    tooltip.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.contains(WAXED) || itemString.contains("sign")) {
                    tooltip.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
            if (blockState.contains(DEGRADATION)) {
                    tooltip.add(Text.translatable("tag.block.immersive_interactions.oxidizable_blocks").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY));
            }
        }
    }
}
