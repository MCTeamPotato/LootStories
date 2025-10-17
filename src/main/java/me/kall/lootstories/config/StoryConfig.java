package me.kall.lootstories.config;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.jsonate.api.JsonConfig;
import me.kall.lootstories.LootStories;
import me.kall.lootstories.data.Story;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.Map;

public class StoryConfig implements IConfig {
    private final JsonConfig storyConfig = JsonConfig.create(LootStories.MOD_ID, "3")
            .put("BookLootPossibility(%)", 60)
            .put("StoryWeight", Lists.newArrayList())
            .put("ChestBindStory", Lists.newArrayList())
            .initialize();

    private final Object2IntMap<String> storyWeight = new Object2IntOpenHashMap<>();
    private final int possibility = storyConfig.getInt("BookLootPossibility(%)");
    private final Map<ResourceLocation, ShufflingList<Story>> bindStories = new Object2ObjectOpenHashMap<>();

    @Override
    public int possibility() {
        return this.possibility;
    }

    @Override
    public Object2IntMap<String> storyWeight() {
        return this.storyWeight;
    }

    @Override
    public Map<ResourceLocation, ShufflingList<Story>> bindStories() {
        return this.bindStories;
    }

    @Override
    public void initStoryWeight() {
        this.storyConfig.getStream("StoryWeight", String.class).forEach(entry -> {
            String[] parts = entry.split(";");
            String title = parts[0];
            int weight = Integer.parseInt(parts[1]);
            this.storyWeight.put(title, weight);
            LootStories.LOGGER.info("{} weighted {} get loaded", title, weight);
        });
    }

    @Override
    public void initBindStories() {
        for (Story story : LootStories.STORIES) {
            this.storyConfig.getStream("ChestBindStory", String.class).forEach(entry -> {
                String[] parts = entry.split(";");
                ResourceLocation lootTable = ResourceLocation.parse(parts[0]);
                String title = parts[1];
                if (story.info().title().equals(title)){
                    this.bindStories.computeIfAbsent(lootTable, key -> new ShufflingList<>()).add(story, LootStories.getWeight(title));
                }
            });
        }
    }
}
