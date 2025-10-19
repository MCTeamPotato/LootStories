package me.kall.lootstories.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.lootstories.LootStories;
import me.kall.lootstories.data.Story;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class KeyManager {

    private final Map<Story, ResourceLocation> storyToKey = new Object2ObjectOpenHashMap<>();
    private final Map<ResourceLocation, Story> keyToStory = new Object2ObjectOpenHashMap<>();

    public KeyManager(@NotNull String modId, @NotNull List<Story> stories) {
        for (Story story : stories) {
            ResourceLocation storyKey = generateKey(modId, story);
            storyToKey.put(story, storyKey);
            keyToStory.put(storyKey, story);
            LootStories.LOGGER.info("[LootStories] Registered keys {} for '{}' by '{}'", storyKey, story.info().title(), story.info().author());
        }
    }

    private static @NotNull ResourceLocation generateKey(String modId, @NotNull Story story) {
        String title = story.info().title().toLowerCase().replaceAll("\\s+", "_");
        title = encode(title);
        int contentHash = story.content().hashCode();
        return ResourceLocation.fromNamespaceAndPath(modId, title + "_" + Integer.toHexString(contentHash));
    }

    private static @NotNull String encode(@NotNull String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.') {
                sb.append(c);
            } else {
                sb.append(String.format("%04x", (int) c));
            }
        }
        return sb.toString();
    }

    public @NotNull ResourceLocation getKey(@NotNull Story story) {
        return storyToKey.get(story);
    }

    public @NotNull Story getStory(@NotNull ResourceLocation key) {
        return keyToStory.get(key);
    }
}
