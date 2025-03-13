package com.lilys_vanilla.mixin;

import com.lilys_vanilla.datagen.ModBlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectShearing(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        String blockIdString = Registries.BLOCK.getId(block).toString();

        if (!world.isClient && state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS)) {
            String newBlockId = blockIdString.replace("mossy_", "");
            Block newBlock = Registries.BLOCK.get(Identifier.of(newBlockId));

            if (newBlock != null) {
                BlockState newState = newBlock.getDefaultState();
                for (Property<?> property : state.getProperties()) {
                    if (newState.contains(property)) {
                        newState = copyProperty(newState, state, property);
                    }
                }

                world.setBlockState(pos, newState);
                world.playSound(null, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS);
                context.getStack().damage(1, ((ServerWorld) world), ((ServerPlayerEntity) context.getPlayer()),
                        item -> context.getPlayer().sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                Block.dropStack(world, pos, new ItemStack(Items.MOSS_CARPET));

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Unique
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }
}
