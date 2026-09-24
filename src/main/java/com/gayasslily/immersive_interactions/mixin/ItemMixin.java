package com.gayasslily.immersive_interactions.mixin;

import com.gayasslily.immersive_interactions.datagen.ModBlockTagProvider;
import com.gayasslily.immersive_interactions.datagen.ModItemTagProvider;
import com.gayasslily.immersive_interactions.item.ModItems;
import com.gayasslily.immersive_interactions.item.custom.ChiselItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.*;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.gayasslily.immersive_interactions.ImmersiveInteractions.*;

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
            if (identifier.contains("chiseled_bookshelf")) return true;
            return !(identifier.matches(".*(exposed_|weathered_|oxidized_|waxed_).*") || hasUncrackedVariant(identifier) || hasUnmossedVariant(identifier) || hasUnchiseledVariant(identifier));
        }
        if ((Object) this instanceof Item) return !((identifier.contains("_minecart") && !identifier.equals("minecart")) || identifier.matches(".*(chest_|cannon_)(boat|raft).*"));
        return true;
    }

    @Shadow @Final @Mutable
    private ComponentMap components;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeBoatsAndMinecartsStackable(Item.Settings settings, CallbackInfo ci) {
        Item self = (Item) (Object) this;
        int newStackSize = -1;

        if (self instanceof BoatItem) newStackSize = 16;
        else if (self instanceof MinecartItem) newStackSize = 16;

        if (newStackSize > 0) {
            ComponentMap override = ComponentMap.builder().add(DataComponentTypes.MAX_STACK_SIZE, newStackSize).build();
            components = ComponentMap.of(components, override);
        }
    }

    @Unique
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }

    @WrapMethod(method = "useOnBlock")
    private ActionResult thumbandthicket$useOnBlock(ItemUsageContext context, Operation<ActionResult> original) {
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        String blockString = Registries.BLOCK.getId(state.getBlock()).getPath();
        Item barkFD = Registries.ITEM.get(Identifier.of("farmersdelight", "tree_bark"));

        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            SoundEvent soundEvent = null;
            String prependString = "";
            String appendString = "";
            String targetString = "";
            String replaceString = "";
            ItemStack stack = ItemStack.EMPTY;
            String newBlockString = blockString;

            if (itemStack.getItem() instanceof PickaxeItem && state.isIn(ModBlockTagProvider.CRACKABLE_BLOCKS)) {
                prependString = "cracked_";
                soundEvent = SoundEvents.BLOCK_DEEPSLATE_BRICKS_HIT;
            }
            if (state.isIn(ModBlockTagProvider.CRACKED_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_REPAIR_BRICK) && !blockString.contains("infested")) {
                targetString = "cracked_";
                soundEvent = SoundEvents.BLOCK_MUD_STEP;
            }
            if (state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS) && itemStack.isIn(ModItemTagProvider.CAN_APPLY_MOSS)) {
                prependString = "mossy_";
                soundEvent = SoundEvents.BLOCK_MOSS_HIT;
            }
            if (itemStack.getItem() instanceof ShearsItem && state.isIn(ModBlockTagProvider.MOSSY_BLOCKS)) {
                targetString = "mossy_";
                soundEvent = SoundEvents.BLOCK_GROWING_PLANT_CROP;
                stack = ModItems.MOSS_CLUMP.getDefaultStack();
            }
            if (state.isIn(BlockTags.LOGS) && (itemStack.isIn(ModItemTagProvider.CAN_APPLY_BARK) || itemStack.isOf(barkFD))) {
                if (blockString.contains("stripped_")) {
                    targetString = "stripped_";
                }
                if (blockString.contains("_log") && !blockString.contains("stripped_")) {
                    targetString = "_log";
                    replaceString = "_wood";
                }
                soundEvent = SoundEvents.BLOCK_WOOD_HIT;
            }
            if (itemStack.getItem() instanceof ChiselItem) {
                soundEvent = SoundEvents.BLOCK_GRINDSTONE_USE;
                if (state.isIn(ModBlockTagProvider.CHISELABLE_BLOCKS)) {
                    prependString = "chiseled_";
                    if (state.getBlock() instanceof Oxidizable oxidizable) {
                        if (oxidizable.getDegradationLevel().ordinal() > 0){
                            String[] copperBlock = blockString.split("_");
                            prependString = "";
                            newBlockString = copperBlock[0] + "_chiseled_" + copperBlock[1];
                        }
                        if (blockString.contains("block")) newBlockString = "copper";
                    }
                    if (blockString.contains("waxed")) {
                        String[] waxedCopperBlock = blockString.split("_");
                        prependString = "";
                        newBlockString = waxedCopperBlock[0] + "_" + waxedCopperBlock[1] + "_chiseled_" + waxedCopperBlock[2];
                        if (blockString.contains("block")) newBlockString = waxedCopperBlock[0] + "_chiseled_" + waxedCopperBlock[1];
                    }
                }
                if (state.isIn(ModBlockTagProvider.CHISELED_BLOCKS)) {
                    targetString = "chiseled_";
                    if (blockString.equals("waxed_chiseled_copper")) appendString = "_block";
                    if (state.getBlock() instanceof Oxidizable && blockString.equals("chiseled_copper")) appendString = "_block";
                }
            }
            if (itemStack.getItem() instanceof DyeItem dyeItem && !blockString.matches(".*(bed|shulker|banner).*")) {
                DyeColor color = dyeItem.getColor();
                String[] splitBlockString = blockString.split("_");

                soundEvent = SoundEvents.ITEM_GLOW_INK_SAC_USE;
                replaceString = color.asString() + "_";
                if (state.isIn(ConventionalBlockTags.DYED)) targetString = splitBlockString[0] + "_";
                if (state.isIn(ModBlockTagProvider.DYEABLE_BASE_BLOCKS)) prependString = color.asString() + "_";
                if (state.isOf(Blocks.GLASS) || state.isOf(Blocks.GLASS_PANE)) prependString = prependString + "stained_";
            }

            if(!targetString.isEmpty()) newBlockString = newBlockString.replace(targetString, replaceString);
            if(!prependString.isEmpty()) newBlockString = prependString + newBlockString;
            if(!appendString.isEmpty()) newBlockString = newBlockString + appendString;

            Block newBlock = getBlockByName(newBlockString);
            if (!(newBlock instanceof AirBlock) && newBlock != state.getBlock()) {
                BlockState newState = newBlock.getDefaultState();
                for (Property<?> property : state.getProperties()) if (newState.contains(property)) newState = copyProperty(newState, state, property);

                if (newBlock instanceof ChiseledBookshelfBlock) newState = newState.with(HorizontalFacingBlock.FACING, context.getHorizontalPlayerFacing().getOpposite());

                world.setBlockState(pos, newState);
                if (soundEvent != null) world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS);
                if (itemStack.contains(DataComponentTypes.MAX_DAMAGE)) itemStack.damage(1, (ServerWorld) world, (ServerPlayerEntity) context.getPlayer(), item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                if (!stack.isEmpty()) Block.dropStack(world, pos, stack);
                else itemStack.decrementUnlessCreative(1, player);
            }
            world.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH, pos, GameEvent.Emitter.of(player));
            return ActionResult.success(!(newBlock instanceof AirBlock) && !Objects.equals(blockString, newBlockString));
        }
        return original.call(context);
    }
}
