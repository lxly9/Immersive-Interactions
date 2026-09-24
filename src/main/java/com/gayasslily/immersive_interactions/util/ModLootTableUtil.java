package com.gayasslily.immersive_interactions.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static com.gayasslily.immersive_interactions.ImmersiveInteractions.MOD_ID;

public class ModLootTableUtil {
    private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();

    public static final RegistryKey<LootTable> REMOVE_MOSS_LOOT = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "gameplay/remove_moss"));
    public static final RegistryKey<LootTable> PATINA_LOOT = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "gameplay/patina"));

    public static final LootContextType REMOVE_MOSS = register(Identifier.of(MOD_ID, "gameplay/remove_moss"), builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE));
    public static final LootContextType PATINA = register(Identifier.of(MOD_ID, "gameplay/patina"), builder -> builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE));

    private static LootContextType register(Identifier identifier, Consumer<LootContextType.Builder> type) {
        LootContextType.Builder builder = new LootContextType.Builder();
        type.accept(builder);
        LootContextType lootContextType = builder.build();
        LootContextType lootContextType2 = MAP.put(identifier, lootContextType);
        if (lootContextType2 != null) {
            throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
        } else {
            return lootContextType;
        }
    }
}