package me.kall.lootstories;

import me.kall.lootstories.config.IConfig;
import me.kall.lootstories.config.StoryConfig;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mod(LootStories.MOD_ID)
public final class LootStories {
    public static final String MOD_ID = "lootstories";
    public static final String MOD_NAME = "LootStories";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final List<StoryEntry> STORIES = loadStories();
    public static final @Nullable IConfig CONFIG = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();

    public static boolean mayGen() {
        return ThreadLocalRandom.current().nextInt(100) <= (CONFIG == null ? 60 : CONFIG.possibility());
    }

    private static @NotNull List<StoryEntry> loadStories() {
        List<StoryEntry> stories = new ArrayList<>();
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve(MOD_ID);

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);

                File modFile = FMLLoader.getLoadingModList().getModFileById(MOD_ID).getFile().getFilePath().toFile();
                if (modFile.exists() && modFile.isFile()) {
                    try (ZipFile zip = new ZipFile(modFile)) {
                        Enumeration<? extends ZipEntry> entries = zip.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            String entryName = entry.getName();

                            if (entryName.startsWith("assets/lootstories/stories/") && entryName.endsWith(".txt")) {
                                Path targetFile = configDir.resolve(Path.of(entryName).getFileName());
                                try (InputStream in = zip.getInputStream(entry)) {
                                    Files.copy(in, targetFile);
                                } catch (Exception e) {
                                    LOGGER.warn("Failed to extract story {}: {}", entryName, e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Failed to open mod jar for story extraction", e);
                    }
                }
            }

            try (var files = Files.list(configDir)) {
                files.filter(f -> f.toString().endsWith(".txt")).forEach(file -> {
                    try {
                        String content = Files.readString(file, StandardCharsets.UTF_8);
                        String filename = file.getFileName().toString();
                        String author = parseAuthorFromFilename(filename);
                        stories.add(new StoryEntry(content, author));
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

    private static @NotNull String parseAuthorFromFilename(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        String nameWithoutExt = dot > 0 ? filename.substring(0, dot) : filename;
        int byIndex = nameWithoutExt.lastIndexOf("by");
        if (byIndex >= 0 && byIndex + 2 < nameWithoutExt.length()) {
            String possibleAuthor = nameWithoutExt.substring(byIndex + 2).trim();
            if (!possibleAuthor.isEmpty()) {
                return possibleAuthor;
            }
        }
        return "";
    }

    public static @NotNull ListTag createPagesFromStory(@NotNull String story) {
        ListTag pages = new ListTag();
        int pageLength = 128;
        int start = 0;

        while (start < story.length()) {
            int end = Math.min(start + pageLength, story.length());
            pages.add(StringTag.valueOf(story.substring(start, end)));
            start = end;
        }

        return pages;
    }

    public record StoryEntry(String content, String author) {}
}
