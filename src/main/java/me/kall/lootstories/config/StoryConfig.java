package me.kall.lootstories.config;

import com.google.common.collect.Lists;
import me.kall.jsonate.api.JsonConfig;
import me.kall.lootstories.LootStories;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryConfig implements IConfig {

    private final JsonConfig storyConfig = JsonConfig.create(LootStories.MOD_ID, "4")
            .put("BookLootPossibility(%)", 60)
            .put("StoryWeight", Lists.newArrayList())
            .put("ChestBindStory", Lists.newArrayList())
            .initialize();

    private final int bookLootChance = storyConfig.getInt("BookLootPossibility(%)");
    private final Map<String, Integer> storyWeights = new HashMap<>();
    private final Map<ResourceLocation, List<String>> boundStoryTitles = new HashMap<>();

    public StoryConfig() {
        initStoryWeights();
        initBoundStories();
    }

    private void initStoryWeights() {
        storyConfig.getStream("StoryWeight", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            storyWeights.put(parts[0], Integer.parseInt(parts[1]));
            LootStories.LOGGER.info("Loaded story weight: {} -> {}", parts[0], parts[1]);
        });
    }

    private void initBoundStories() {
        storyConfig.getStream("ChestBindStory", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            ResourceLocation lootTable = ResourceLocation.parse(parts[0]);
            boundStoryTitles.computeIfAbsent(lootTable, k -> Lists.newArrayList()).add(parts[1]);
        });
    }

    @Override
    public int bookLootChance() {
        return bookLootChance;
    }

    @Override
    public Map<String, Integer> storyWeights() {
        return storyWeights;
    }

    @Override
    public Map<ResourceLocation, List<String>> boundStoryTitles() {
        return boundStoryTitles;
    }
}
