package me.kall.lootstories.config;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public interface IConfig {
    int bookLootChance();

    Map<String, Integer> storyWeights();

    Map<ResourceLocation, List<String>> boundStoryTitles();
}
