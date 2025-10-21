package me.kall.lootstories.config;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.kall.jsonate.api.JsonConfig;
import me.kall.lootstories.LootStories;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class StoryConfig implements IConfig {

    private final JsonConfig storyConfig = JsonConfig.create(LootStories.MOD_ID, "5")
            .put("BookLootPossibility(%)", 100)
            .put("StoryWeight", Lists.newArrayList())
            .put("ChestBindStory", Lists.newArrayList())
            .initialize();

    private final int bookLootChance = storyConfig.getInt("BookLootPossibility(%)");
    private final Object2IntMap<String> storyWeights = new Object2IntOpenHashMap<>();
    private final Map<ResourceLocation, Set<String>> boundStoryTitles = new Object2ObjectOpenHashMap<>();

    public StoryConfig() {
        initStoryWeights();
        initBoundStories();
    }

    private void initStoryWeights() {
        storyConfig.getStream("StoryWeight", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            storyWeights.put(parts[0], Integer.parseInt(parts[1]));
            LootStories.LOGGER.info("[LootStories] Loaded story weight: {} -> {}", parts[0], parts[1]);
        });
    }

    private void initBoundStories() {
        storyConfig.getStream("ChestBindStory", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            boundStoryTitles.computeIfAbsent(ResourceLocation.parse(parts[0]), k -> new ObjectOpenHashSet<>()).add(parts[1]);
            LootStories.LOGGER.info("[LootStories] LootTable {} was linked to story {}", parts[0], parts[1]);
        });
    }

    @Override
    public int bookLootChance() {
        return bookLootChance;
    }

    @Override
    public Object2IntMap<String> storyWeights() {
        return storyWeights;
    }

    @Override
    public Map<ResourceLocation, Set<String>> boundStoryTitles() {
        return boundStoryTitles;
    }
}
