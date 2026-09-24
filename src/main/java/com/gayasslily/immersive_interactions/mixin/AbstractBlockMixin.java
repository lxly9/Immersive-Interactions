package com.gayasslily.immersive_interactions.mixin;

import com.gayasslily.immersive_interactions.datagen.ModBlockTagProvider;
import com.gayasslily.immersive_interactions.util.ModLootTableUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.gayasslily.immersive_interactions.ImmersiveInteractions.getBlockByName;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @Inject(method = "getDroppedStacks", at = @At("HEAD"))
    private void gay(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        ServerWorld world = builder.getWorld();
        Block block = state.getBlock();
        Vec3d origin = builder.get(LootContextParameters.ORIGIN);
        RegistryKey<LootTable> lootTableRegistryKey = null;
        LootContextType lootContextType = null;
        int dropCount = 1;

        if (state.contains(DoorBlock.HALF) && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) return;

        if (state.isIn(ModBlockTagProvider.MOSSY_BLOCKS)) {
            lootTableRegistryKey = ModLootTableUtil.REMOVE_MOSS_LOOT;
            lootContextType = ModLootTableUtil.REMOVE_MOSS;
        }

        if (block.toString().contains("waxed")) block = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block);
        if (block instanceof Oxidizable oxidizable) {
            dropCount = Math.max(1, oxidizable.getDegradationLevel().ordinal());
            lootTableRegistryKey = ModLootTableUtil.PATINA_LOOT;
            lootContextType = ModLootTableUtil.PATINA;
        }

        if (lootContextType != null && lootTableRegistryKey != null) {
            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(lootTableRegistryKey);
            LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).add(LootContextParameters.ORIGIN, origin).add(LootContextParameters.BLOCK_STATE, state).build(lootContextType);
            List<ItemStack> list = lootTable.generateLoot(lootContextParameterSet);

            for (ItemStack stack : list) {
                stack.setCount(stack.getCount() * dropCount);
                Block.dropStack(world, BlockPos.ofFloored(origin), stack);
            }
        }
    }

    @WrapOperation(method = "getDroppedStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock;getLootTableKey()Lnet/minecraft/registry/RegistryKey;"))
    private RegistryKey<LootTable> thumbandthicket$dropDifferentStack(AbstractBlock instance, Operation<RegistryKey<LootTable>> original, @Local(argsOnly = true) BlockState state) {
        Block block = (Block) instance;
        String targetString = "";
        String[] blockString = Registries.BLOCK.getId(block).toString().split(":");

        if (state.isIn(ModBlockTagProvider.CRACKED_BLOCKS) && !blockString[1].contains("infested")) targetString = "cracked_";
        if (state.isIn(ModBlockTagProvider.MOSSY_BLOCKS)) targetString = "mossy_";
        if (state.isIn(ModBlockTagProvider.CHISELED_BLOCKS)) targetString = "chiseled_";
        if (blockString[1].contains("waxed")) block = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block);

        if (block instanceof Oxidizable) return original.call(Oxidizable.getUnaffectedOxidationBlock(block));

        Block block1 = getBlockByName(blockString[1].replace(targetString,""));
        return block1 instanceof AirBlock ? original.call(instance) : original.call(block1);
    }

    @WrapOperation(method = "getDroppedStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContextParameterSet$Builder;add(Lnet/minecraft/loot/context/LootContextParameter;Ljava/lang/Object;)Lnet/minecraft/loot/context/LootContextParameterSet$Builder;"))
    private <T> LootContextParameterSet.Builder gay(LootContextParameterSet.Builder instance, LootContextParameter<T> parameter, T value, Operation<LootContextParameterSet.Builder> original, @Local(argsOnly = true) BlockState state) {
        return state.getBlock() instanceof Oxidizable && state.getBlock() instanceof DoorBlock && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? original.call(instance, parameter, Oxidizable.getUnaffectedOxidationBlock(state.getBlock()).getDefaultState()) : original.call(instance, parameter, value);
    }
}