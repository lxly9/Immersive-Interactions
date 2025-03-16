package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockTransformationHelper {
    private static final Logger log = LoggerFactory.getLogger(BlockTransformationHelper.class);

    private static final String[][] categoryMappings = {
            {"copper_block", "chiseled_copper"},
            {"exposed_copper", "exposed_chiseled_copper"},
            {"weathered_copper", "weathered_chiseled_copper"},
            {"oxidized_copper", "oxidized_chiseled_copper"},
            {"chiseled_copper", "copper_block"},
            {"exposed_chiseled_copper", "exposed_copper"},
            {"weathered_chiseled_copper", "weathered_copper"},
            {"oxidized_chiseled_copper", "oxidized_copper"}
    };

    public static Block findBestMatch(String blockIdString, TagKey<Block> tagKey) {
        Block bestMatch = null;
        int bestScore = Integer.MIN_VALUE;

        boolean isInfested = blockIdString.contains("infested");

        RegistryEntryList<Block> blocksInTag = Registries.BLOCK.getEntryList(tagKey).orElse(null);
        if (blocksInTag == null) return null;

        for (RegistryEntry<Block> entry : blocksInTag) {
            Block tagBlock = entry.value();
            String tagBlockId = Registries.BLOCK.getId(tagBlock).toString();

            if (isInfested != tagBlockId.contains("infested")) continue;

            int score = 0;

            if (blockIdString.equals(tagBlockId)) {
                score += 100;
            }

            for (String[] mapping : categoryMappings) {
                if (blockIdString.contains(mapping[0]) && tagBlockId.contains(mapping[1])) {
                    score += 50;
                }
            }

            int editDistance = levenshteinDistance(blockIdString, tagBlockId);
            score -= editDistance;

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

