package com.lilys_vanilla.item.custom;

import com.lilys_vanilla.datagen.ModBlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MossItem extends Item {

    public MossItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        String blockIdString = Registries.BLOCK.getId(block).toString();

        if (!world.isClient && state.isIn(ModBlockTagProvider.MOSSABLE_BLOCKS)) {
            String[] parts = blockIdString.split(":");
            if (parts.length == 2) {
                String newBlockId = parts[0] + ":mossy_" + parts[1];
                Block newBlock = Registries.BLOCK.get(Identifier.of(newBlockId));

                if (newBlock != null) {
                    BlockState newState = newBlock.getDefaultState();
                    for (Property<?> property : state.getProperties()) {
                        if (newState.contains(property)) {
                            newState = copyProperty(newState, state, property);
                        }
                    }

                    world.setBlockState(pos, newState);
                    world.playSound(null, pos, SoundEvents.BLOCK_MOSS_PLACE, SoundCategory.BLOCKS);
                    context.getStack().decrement(1); // Remove 1 moss carpet
                }
            }
            return ActionResult.SUCCESS;
        }else {
            return super.useOnBlock(context);
        }
    }

    @Unique
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }
}
