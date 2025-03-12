package com.lilys_vanilla.item.custom;

import com.lilys_vanilla.ModTags;
import com.lilys_vanilla.datagen.ModBlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static com.lilys_vanilla.datagen.ModBlockTagProvider.WEATHERABLE_ITEMS;

public class PatinaItem extends Item {

    public PatinaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Block clickedBlock = world.getBlockState(context.getBlockPos()).getBlock();

        // Check if the block is in the copper tag and is not already oxidized
        if (WEATHERABLE_ITEMS.id().equals(clickedBlock)) {
            // Check if the block is not already oxidized
            Block nextBlock = getNextBlockState(clickedBlock);
            if (nextBlock != null) {
                if (!world.isClient()) {
                    // Change the block to the next stage of oxidation
                    world.setBlockState(context.getBlockPos(), nextBlock.getDefaultState());

                    // Play the sound of the patina transformation
                    world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS);

                    // Consume the item by decrementing it from the player's inventory
                    context.getPlayer().getMainHandStack().decrement(1);
                }

                // Trigger the interact animation for the player
                context.getPlayer().swingHand(Hand.MAIN_HAND);

                return ActionResult.SUCCESS;  // Item was successfully used
            }
        }

        return ActionResult.PASS;  // Item cannot be used on this block
    }

    private Block getNextBlockState(Block currentBlock) {
        // Determine the next block in the aging process
        if (currentBlock == Blocks.COPPER_BLOCK) {
            return Blocks.EXPOSED_COPPER;
        } else if (currentBlock == Blocks.EXPOSED_COPPER) {
            return Blocks.WEATHERED_COPPER;
        } else if (currentBlock == Blocks.WEATHERED_COPPER) {
            return Blocks.OXIDIZED_COPPER;
        }

        // If the block is already oxidized or not weatherable, return null
        return null;
    }
}
