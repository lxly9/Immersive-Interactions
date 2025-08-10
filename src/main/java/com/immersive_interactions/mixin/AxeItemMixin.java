package com.immersive_interactions.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import com.immersive_interactions.item.ModItems;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.immersive_interactions.ImmersiveInteractions.*;
import static com.immersive_interactions.util.ModProperties.*;


@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectScrape(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Block block = state.getBlock();
        BlockState defaultState = block.getDefaultState();


        if (!world.isClient && defaultState.contains(DEGRADATION) && player != null) {
            int degradation = state.get(DEGRADATION);
            if (!state.get(WAXED) && degradation > 0){
                if (!player.isCreative()) {
                    Block.dropStack(world, pos, new ItemStack(ModItems.COPPER_PATINA));
                }
                BlockState newState = block.getStateWithProperties(state).with(DEGRADATION, degradation -1);
                world.setBlockState(pos, newState, 11);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                world.syncWorldEvent(WorldEvents.BLOCK_SCRAPED, pos, 0);
                cir.setReturnValue(ActionResult.SUCCESS);
            }

            if (state.get(WAXED)){
                BlockState newState = block.getStateWithProperties(state).with(WAXED, false);
                world.setBlockState(pos, newState, 11);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                world.syncWorldEvent(WorldEvents.WAX_REMOVED, pos, 0);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void injectStripping(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Block block = state.getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();
        Identifier id = Identifier.of("farmersdelight", "tree_bark");
        Item barkFD = Registries.ITEM.get(id);


        if (!world.isClient && state.isIn(BlockTags.LOGS) && player != null) {

            if (!player.isCreative() && !blockIdString.contains("stripped"))  {
                if (isModLoaded("farmersdelight")){
                    Block.dropStack(world, pos, new ItemStack(barkFD));
                }
                else {
                    Block.dropStack(world, pos, new ItemStack(ModItems.BARK));
                }
            }
        }
    }
}
