package me.kall.lootstories.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.data.Story;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class StoryManager {

    private final ShufflingList<Story> allStories = new ShufflingList<>();
    private final Map<ResourceLocation, ShufflingList<Story>> boundStories = new Object2ObjectOpenHashMap<>();
    private final @Nullable IConfig config;

    public StoryManager(@Nullable IConfig config, List<Story> stories) {
        this.config = config;
        loadStories(stories);
        bindStories();
    }

    private void loadStories(@NotNull List<Story> stories) {
        for (Story story : stories) {
            allStories.add(story, config == null ? 10 : config.storyWeights().getOrDefault(story.info().title(), 10));
        }
    }

    private void bindStories() {
        if (config == null) return;
        for (var entry : config.boundStoryTitles().entrySet()) {
            ResourceLocation lootTable = entry.getKey();
            ShufflingList<Story> list = new ShufflingList<>();
            for (String title : entry.getValue()) {
                allStories.shuffle().stream()
                        .filter(s -> s.info().title().equals(title))
                        .findFirst()
                        .ifPresent(story -> list.add(story, config.storyWeights().getOrDefault(title, 10)));
            }
            if (list.iterator().hasNext()) boundStories.put(lootTable, list);
        }
    }

    public Story getRandomStory(@Nullable ResourceLocation lootTable) {
        ShufflingList<Story> list = lootTable != null ? boundStories.get(lootTable) : null;
        if (list != null && list.iterator().hasNext()) return getRandom(list);
        return getRandom(allStories);
    }

    private Story getRandom(@NotNull ShufflingList<Story> list) {
        return list.shuffle().stream().findFirst().orElseThrow();
    }

    public boolean mayGenerateBook() {
        return ThreadLocalRandom.current().nextInt(100) < (config == null ? 100 : config.bookLootChance()) && this.allStories.iterator().hasNext();
    }
}
