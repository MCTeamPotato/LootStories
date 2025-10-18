package me.kall.lootstories;

import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.config.StoryConfig;
import me.kall.lootstories.data.Info;
import me.kall.lootstories.data.Story;
import me.kall.lootstories.utils.Extractor;
import me.kall.lootstories.utils.KeyManager;
import me.kall.lootstories.utils.StoryManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(LootStories.MOD_ID)
public final class LootStories {

    public static final String MOD_ID = "lootstories";
    public static final Logger LOGGER = LogManager.getLogger("LootStories");
    public static final @Nullable IConfig CONFIG = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();
    public static final Map<String, List<Story>> STORIES = loadStories();
    public static final StoryManager STORY_MANAGER = new StoryManager(CONFIG, STORIES.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
    public static final KeyManager KEY_MANAGER = new KeyManager(MOD_ID, STORIES.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

    private static @NotNull Map<String, List<Story>> loadStories() {
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve(MOD_ID);
        Map<String, List<Story>> map = new java.util.HashMap<>();

        try {
            if (!Files.exists(configDir))
                Extractor.extractJar(MOD_ID, "assets/lootstories/stories/", configDir, false);

            try (Stream<Path> langDirs = Files.list(configDir)) {
                langDirs.filter(Files::isDirectory).forEach(langDir -> {
                    String lang = langDir.getFileName().toString();
                    try (Stream<Path> files = Files.list(langDir)) {
                        List<Story> stories = files.filter(f -> f.toString().endsWith(".txt"))
                                .map(file -> {
                                    try {
                                        return new Story(Info.parse(file.getFileName().toString()), Files.readString(file));
                                    } catch (Exception e) {
                                        LOGGER.warn("[LootStories] Failed to read story file {} for lang {}", file, lang, e);
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        if (!stories.isEmpty()) map.put(lang, stories);
                    } catch (Exception e) {
                        LOGGER.warn("[LootStories] Failed to list files in lang dir {}", langDir, e);
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error("[LootStories] Failed to load stories", e);
        }

        return map;
    }


    static {
        if (CONFIG != null) readme();
    }

    private static void readme() {
        Extractor.extractJar(MOD_ID, "assets/lootstories/readme/", FMLLoader.getGamePath().resolve("config"), true);
    }
}
