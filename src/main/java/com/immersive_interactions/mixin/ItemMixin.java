package com.immersive_interactions.mixin;

import com.immersive_interactions.datagen.ModBlockTagProvider;
import com.immersive_interactions.datagen.ModItemTagProvider;
import com.immersive_interactions.item.ModItems;
import com.immersive_interactions.item.custom.ChiselItem;
import com.immersive_interactions.item.custom.PatinaItem;
import com.immersive_interactions.util.DynamicEntityTooltipHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.*;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
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

import java.util.*;

import static com.immersive_interactions.ImmersiveInteractions.*;
import static com.immersive_interactions.util.BlockTransformationHelper.*;
import static com.immersive_interactions.util.DyeMatcher.dyedBlockMatcher;
import static com.immersive_interactions.util.WoodTransformationHelper.transformLogToWood;

@Mixin(Item.class)
public abstract class ItemMixin implements ToggleableFeature {

    @Shadow
    private final RegistryEntry.Reference<Item> registryEntry = Registries.ITEM.createEntry((Item) (Object) this);

    @Unique
    public boolean isEnabled(FeatureSet enabledFeatures) {
        var key = registryEntry.getKey().get();
        Identifier Id = key.getValue();
        String identifier = Id.toString();

        if ((Object) this instanceof BlockItem) {

            if (identifier.contains("chiseled_bookshelf")) {
                return true;
            }

            return !(identifier.contains(".*(exposed_|weathered_|oxidized_|waxed_).*") || hasUncrackedVariant(identifier) || hasUnmossedVariant(identifier) || hasUnchiseledVariant(identifier));
        }
        if ((Object) this instanceof Item){
            return !((identifier.contains("_minecart") && !identifier.equals("minecart")) || identifier.matches(".*(chest_|cannon_)(boat|raft).*"));
        }
        return true;
    }

    @Shadow @Final @Mutable
    private ComponentMap components;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeBoatsAndMinecartsStackable(Item.Settings settings, CallbackInfo ci) {
        Item self = (Item) (Object) this;
        int newStackSize = -1;

        if (self instanceof BoatItem) {
            newStackSize = 16;
        } else if (self instanceof MinecartItem) {
            newStackSize = 16;
        }

        if (newStackSize > 0) {
            ComponentMap override = ComponentMap.builder()
                    .add(DataComponentTypes.MAX_STACK_SIZE, newStackSize)
                    .build();

            components = ComponentMap.of(components, override);
        }
    }

    @WrapMethod(method = "useOnBlock")
    private ActionResult immersive_interactions$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        Identifier itemId = Registries.ITEM.getId(context.getStack().getItem());
        World world = context.getWorld();
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String identifier = blockId.toString();
        String blockIdString = Registries.BLOCK.getId(block).toString();
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);

        if (!world.isClient){
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            //Crack
            if (itemStack.getItem() instanceof PickaxeItem && hasCrackedVariant(identifier)) {
                Block crackedId = getBlockByName("cracked_", identifier);

                world.setBlockState(pos, crackedId.getStateWithProperties(state), 11);
                world.playSound(null, pos, SoundEvents.BLOCK_DEEPSLATE_BRICKS_HIT, SoundCategory.BLOCKS);
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }

            //Uncrack
            if (itemStack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK) && hasUncrackedVariant(identifier)) {
                Block unCrackedId = getBlockByName("", identifier.replace("cracked_", ""));

                world.setBlockState(pos, unCrackedId.getStateWithProperties(state), 11);
                world.playSound(null, pos, SoundEvents.BLOCK_MUD_STEP, SoundCategory.BLOCKS);
                context.getStack().decrementUnlessCreative(1, player);
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }

            //Moss
            if (itemStack.isIn(ModItemTagProvider.CAN_APPLY_MOSS) && hasMossyVariant(identifier)) {
                Block mossyId = getBlockByName("mossy_", identifier);

                world.setBlockState(pos, mossyId.getStateWithProperties(state), 11);
                world.playSound(null, pos, SoundEvents.BLOCK_MOSS_HIT, SoundCategory.BLOCKS);
                context.getStack().decrementUnlessCreative(1, player);
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }

            //Unmoss
            if (itemStack.getItem() instanceof ShearsItem && hasUnmossedVariant(identifier)) {
                Block unmossedID = getBlockByName("", identifier.replace("mossy_", ""));

                world.setBlockState(pos, unmossedID.getStateWithProperties(state), 11);
                world.playSound(null, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS);
                Block.dropStack(world, pos, new ItemStack(ModItems.MOSS_CLUMP));
                context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.SUCCESS;
            }

            //Chisel
            if (itemStack.getItem() instanceof ChiselItem) {
                Block chiseledBlock;

                if (hasChiseledVariant(identifier))  {
                    String chiseledId = identifier;

                    if (identifier.contains("copper")) chiseledId = identifier.replace("copper","chiseled_copper");
                    chiseledBlock = getBlockByName("chiseled_", chiseledId);

                    if (chiseledBlock.getDefaultState().contains(Properties.HORIZONTAL_FACING) && playerEntity != null) {
                        world.setBlockState(pos, chiseledBlock.getStateWithProperties(state).with(Properties.HORIZONTAL_FACING, playerEntity.getHorizontalFacing().getOpposite()), 11);
                    } else {
                        world.setBlockState(pos, chiseledBlock.getStateWithProperties(state), 11);
                    }

                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.SUCCESS;

                }
                //Unchisel
                if (hasUnchiseledVariant(identifier) && !state.contains(Properties.SLOT_0_OCCUPIED)) {

                    chiseledBlock = getBlockByName("", identifier.replace("chiseled_",""));

                    world.setBlockState(pos, chiseledBlock.getStateWithProperties(state), 11);
                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
                    context.getStack().damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                    return ActionResult.SUCCESS;
                }
            }

            //Slime
            if (state.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_SLIME)) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.SLIMY_BLOCKS, world);
                if (newBlock != null) {
                    if (block instanceof PistonBlock) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state).with(Properties.EXTENDED, false), 11);
                    } else {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state), 11);
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrementUnlessCreative(1, player);
                    return ActionResult.SUCCESS;
                }
            }

            //Amethyst
            if (state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_AMETHYST)) {
                Block newBlock = findBestMatch(blockIdString, ModBlockTagProvider.AMETHYST_BLOCKS, world);

                if (newBlock != null) {
                    world.setBlockState(pos, newBlock.getStateWithProperties(state), 11);
                    world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrementUnlessCreative(1, player);
                }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(state.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS));
            }

            //Logs
            if (state.isIn(BlockTags.LOGS) && (itemStack.isIn(ModItemTagProvider.CAN_APPLY_BARK) || itemStack.isOf(barkFD))) {
                Block newBlock = transformLogToWood(blockIdString);
                String clickedWood = block.toString();

                if (newBlock != null) {
                    world.setBlockState(pos, newBlock.getStateWithProperties(state), 11);
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS);
                    context.getStack().decrementUnlessCreative(1, player);
                }
                world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
                return ActionResult.success(clickedWood.contains("stripped") || clickedWood.contains("log"));
            }

            //Dye
            if (itemStack.getItem() instanceof DyeItem) {
                Block newBlock = dyedBlockMatcher(blockId, itemId);


                if (newBlock != Blocks.AIR && !newBlock.toString().matches(".*(bed|shulker|banner).*")) {
                    String dyeColor = itemStack.getItem().toString().replace("dye", "");

                    if (!state.toString().contains(dyeColor)) {
                        world.setBlockState(pos, newBlock.getStateWithProperties(state), 11);
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

        DynamicEntityTooltipHelper.addEntityInteractionTooltip(stack, tooltip);

        if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            BlockState blockState = block.getDefaultState();
            String blockId = Registries.BLOCK.getId(block).toString();
            List<Text> variants = new ArrayList<>();

            if (hasCrackedVariant(blockId)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.crackable_blocks"));
            }
            if (hasMossyVariant(blockId)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.mossable_blocks"));
            }
            if (hasChiseledVariant(blockId)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.chiselable_blocks"));
            }
            if (blockState.isIn(ModBlockTagProvider.SLIMABLE_BLOCKS)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.slimable_blocks"));
            }
            if (blockState.isIn(ModBlockTagProvider.AMETHYSTABLE_BLOCKS)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.amethystable_blocks"));
            }

            Optional<RegistryEntry<PointOfInterestType>> optional = PointOfInterestTypes.getTypeForState(blockState);
            if (isModLoaded("waxed_workstations") && optional.isPresent() && optional.get().isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)) {
                variants.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks"));
            }
            if (isOxidizable(block.getClass()) || stack.getItem() instanceof SignItem || stack.getItem() instanceof HangingSignItem) {
                variants.add(Text.translatable("tag.block.immersive_interactions.waxable_blocks"));
            }

            if (!variants.isEmpty()) {
                MutableText tooltipLine = Text.translatable("tooltip.immersive_interactions.can_be").formatted(Formatting.ITALIC, Formatting.DARK_GRAY).append(" ").append(joinWithAnd(variants));

                tooltip.add(tooltipLine);
            }

            if (isOxidizable(block.getClass())) {
                tooltip.add(Text.translatable("tag.block.immersive_interactions.oxidizable_blocks").formatted(Formatting.ITALIC, Formatting.DARK_GRAY));
            }
        }
    }
}
