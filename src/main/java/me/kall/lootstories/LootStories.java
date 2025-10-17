package me.kall.lootstories;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.config.StoryConfig;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mod(LootStories.MOD_ID)
public final class LootStories {
    public static final String MOD_ID = "lootstories";
    public static final String MOD_NAME = "LootStories";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final ShufflingList<Story> STORIES = new ShufflingList<>();
    public static final @Nullable IConfig CONFIG = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();

    static {
        loadStories().forEach(story -> {
            STORIES.add(story, getWeight(story.info.title));
            LOGGER.info("Story loaded: {}", story.info.title);
        });
    }

    private static int getWeight(String title) {
        return CONFIG == null ? 10 : CONFIG.storyWeight().getOrDefault(title, 10);
    }

    public static boolean mayGen() {
        return ThreadLocalRandom.current().nextInt(100) <= (CONFIG == null ? 100 : CONFIG.possibility());
    }

    public static Story randomStory() {
        return STORIES.shuffle().stream().findFirst().orElseThrow();
    }

    private static @NotNull List<Story> loadStories() {
        List<Story> stories = new ObjectArrayList<>();
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve(MOD_ID);

        try {
            if (!Files.exists(configDir)) extract(configDir);

            try (Stream<Path> files = Files.list(configDir)) {
                files.filter(f -> f.toString().endsWith(".txt")).forEach(file -> {
                    try {
                        stories.add(new Story(parse(file.getFileName().toString()), Files.readString(file, StandardCharsets.UTF_8)));
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

    private static void extract(Path configDir) throws IOException {
        Files.createDirectories(configDir);
        File modFile = FMLLoader.getLoadingModList().getModFileById(MOD_ID).getFile().getFilePath().toFile();
        if (!modFile.exists() || !modFile.isFile()) return;
        try (ZipFile zip = new ZipFile(modFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (!entryName.startsWith("assets/lootstories/stories/")) continue;
                if (!entryName.endsWith(".txt")) continue;
                Path targetFile = configDir.resolve(Path.of(entryName).getFileName());
                try (InputStream in = zip.getInputStream(entry)) {
                    Files.copy(in, targetFile);
                } catch (Exception e) {
                    LOGGER.warn("Failed to extract story {}: {}", entryName, e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to open mod jar for story extraction", e);
        }
    }

    private static @NotNull LootStories.Info parse(@Nullable String filename) {
        if (filename == null) return new Info("", "");

        int dot = filename.lastIndexOf('.');
        String nameWithoutExt = dot > 0 ? filename.substring(0, dot) : filename;

        int byIndex = nameWithoutExt.lastIndexOf("by");
        if (byIndex > 0) {
            String title = nameWithoutExt.substring(0, byIndex).trim();
            String author = nameWithoutExt.substring(byIndex + 2).trim();
            if (author.isEmpty()) author = "unknown";
            return new Info(title, author);
        }

        return new Info(nameWithoutExt, "unknown");
    }

    public record Info(String title, String author) {}
    public record Story(Info info, String content)  {}
}