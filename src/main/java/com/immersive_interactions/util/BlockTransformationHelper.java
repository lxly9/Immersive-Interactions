package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import net.minecraft.world.World;
import static com.immersive_interactions.ImmersiveInteractions.LOGGER;

public class BlockTransformationHelper {

    public static Block findBestMatch(String blockIdString, TagKey<Block> tagKey, World world) {
        Block bestMatch = null;
        int bestScore = Integer.MIN_VALUE;

        boolean isInfested = blockIdString.contains("infested");

        DynamicRegistryManager worldRegistry = world.getRegistryManager();
        Registry<Block> blockRegistry = worldRegistry.get(RegistryKeys.BLOCK);
        RegistryEntryList<Block> blocksInTag = blockRegistry.getEntryList(tagKey).orElse(null);
        if (blocksInTag == null) return null;

        for (RegistryEntry<Block> entry : blocksInTag) {
            Block tagBlock = entry.value();
            String[] tagBlockId = Registries.BLOCK.getId(tagBlock).toString().split(":");
            String tagBlockString = tagBlockId[1];

            if (isInfested != tagBlockString.contains("infested")) continue;

            int score = 0;

            if (blockIdString.equals(tagBlockString)) {
                score += 1000;
            }

            String[] originalParts = blockIdString.split("[/:_]");
            String[] candidateParts = tagBlockString.split("[/:_]");

            int sharedParts = 0;
            for (String part : originalParts) {
                for (String other : candidateParts) {
                    if (part.equals(other)) {
                        sharedParts++;
                        break;
                    }
                }
            }
            score += sharedParts * 20;

            int editDistance = levenshteinDistance(blockIdString, tagBlockString);
            score -= Math.min(editDistance, 10);


            if (score > bestScore) {
                bestScore = score;
                bestMatch = tagBlock;
            }
        }
        return bestMatch;
    }

    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(
                                    dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[len1][len2];
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }
}
