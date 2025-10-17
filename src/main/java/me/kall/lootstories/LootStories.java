package me.kall.lootstories;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.config.StoryConfig;
import me.kall.lootstories.data.Info;
import me.kall.lootstories.data.Story;
import me.kall.lootstories.utils.Extractor;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Mod(LootStories.MOD_ID)
public final class LootStories {
    public static final String MOD_ID = "lootstories";
    public static final String MOD_NAME = "LootStories";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final ShufflingList<Story> STORIES = new ShufflingList<>();
    public static final @Nullable IConfig CONFIG = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();

    static {
        if (CONFIG != null) CONFIG.initStoryWeight();
        loadStories().forEach(story -> {
            STORIES.add(story, getWeight(story.info().title()));
            LOGGER.info("Story loaded: {}", story.info().title());
        });
        if (CONFIG != null) CONFIG.initBindStories();
        Extractor.extractJar(LootStories.MOD_ID, "assets/lootstories/readme/", FMLLoader.getGamePath().resolve("config").resolve(LootStories.MOD_ID));
    }

    public static int getWeight(String title) {
        return CONFIG == null ? 10 : CONFIG.storyWeight().getOrDefault(title, 10);
    }

    private static @NotNull List<Story> loadStories() {
        List<Story> stories = new ObjectArrayList<>();
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve(MOD_ID);

        try {
            if (!Files.exists(configDir)) Extractor.extractJar(MOD_ID, "assets/lootstories/stories/", configDir);

            try (Stream<Path> files = Files.list(configDir)) {
                files.filter(f -> f.toString().endsWith(".txt")).forEach(file -> {
                    try {
                        stories.add(new Story(Info.parse(file.getFileName().toString()), Files.readString(file, StandardCharsets.UTF_8)));
                    } catch (Exception e) {
                        LOGGER.warn("Reading failed: {}", file, e);
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error("Loading failed", e);
        }

        return stories;
    }
}