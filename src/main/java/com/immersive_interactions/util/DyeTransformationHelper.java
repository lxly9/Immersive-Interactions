package com.immersive_interactions.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DyeTransformationHelper {
    private static final Logger log = LoggerFactory.getLogger(DyeTransformationHelper.class);

    private static final String[][] categoryMappings = {
            {"white_dye", "white"},
            {"light_gray_dye", "light_gray"},
            {"gray_dye", "gray"},
            {"black_dye", "black"},
            {"brown_dye", "brown"},
            {"red_dye", "red"},
            {"orange_dye", "orange"},
            {"yellow_dye", "yellow"},
            {"lime_dye", "lime"},
            {"green_dye", "green"},
            {"cyan_dye", "cyan"},
            {"light_blue_dye", "light_blue"},
            {"blue_dye", "blue"},
            {"purple_dye", "purple"},
            {"magenta_dye", "magenta"},
            {"pink_dye", "pink"}
    };

    public static Block findDyeMatch(String blockIdString, TagKey<Block> tagKey, ItemStack stack) {
        Block bestMatch = null;
        int bestScore = Integer.MIN_VALUE;

        RegistryEntryList<Block> blocksInTag = Registries.BLOCK.getEntryList(tagKey).orElse(null);
        if (blocksInTag == null) return null;

        for (RegistryEntry<Block> entry : blocksInTag) {
            Block tagBlock = entry.value();
            String tagBlockId = Registries.BLOCK.getId(tagBlock).toString();

            int score = 0;

            if (blockIdString.equals(tagBlockId)) {
                score += 100;
            }

            for (String[] mapping : categoryMappings) {
                if (stack.toString().contains(mapping[0]) && tagBlockId.contains(mapping[1])) {
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
        log.info(String.valueOf(bestMatch));
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

