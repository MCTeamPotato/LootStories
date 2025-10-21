package me.kall.lootstories.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

public interface IConfig {
    int bookLootChance();

    Object2IntMap<String> storyWeights();

    Map<ResourceLocation, Set<String>> boundStoryTitles();
}
