package me.kall.lootstories;

import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.config.StoryConfig;
import me.kall.lootstories.data.Info;
import me.kall.lootstories.data.Story;
import me.kall.lootstories.utils.Extractor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(LootStories.MOD_ID)
public final class LootStories {

    public static final String MOD_ID = "lootstories";
    public static final Logger LOGGER = LogManager.getLogger("LootStories");
    public static final @Nullable IConfig CONFIG = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();
    public static final StoryManager STORY_MANAGER = new StoryManager(CONFIG, loadStories());

    static {
        if (CONFIG != null) readme();
    }

    private static List<Story> loadStories() {
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve(MOD_ID);
        try {
            if (!Files.exists(configDir)) Extractor.extractJar(MOD_ID, "assets/lootstories/stories/", configDir, false);
            try (Stream<Path> files = Files.list(configDir)) {
                return files.filter(f -> f.toString().endsWith(".txt"))
                        .map(file -> {
                            try {
                                return new Story(Info.parse(file.getFileName().toString()), Files.readString(file));
                            } catch (Exception e) {
                                LOGGER.warn("Failed to read story file {}", file, e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load stories", e);
        }
        return List.of();
    }

    private static void readme() {
        Extractor.extractJar(MOD_ID, "assets/lootstories/readme/", FMLLoader.getGamePath().resolve("config"), true);
    }
}
