package me.kall.lootstories.config;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.kall.jsonate.api.JsonConfig;
import me.kall.lootstories.LootStories;

public class StoryConfig implements IConfig {
    private final JsonConfig storyConfig = JsonConfig.create(LootStories.MOD_ID, "2")
            .put("BookLootPossibility(%)", 60)
            .put("StoryWeight", Lists.newArrayList())
            .initialize();

    private final Object2IntMap<String> storyWeight = new Object2IntOpenHashMap<>();
    private final int possibility = storyConfig.getInt("BookLootPossibility(%)");

    public StoryConfig() {
        this.storyConfig.getStream("StoryWeight", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            String bookTitle = parts[0];
            int weight = Integer.parseInt(parts[1]);
            this.storyWeight.put(bookTitle, weight);
            LootStories.LOGGER.info("{} weighted {} get loaded", bookTitle, weight);
        });
    }

    @Override
    public int possibility() {
        return this.possibility;
    }

    @Override
    public Object2IntMap<String> storyWeight() {
        return this.storyWeight;
    }
}
