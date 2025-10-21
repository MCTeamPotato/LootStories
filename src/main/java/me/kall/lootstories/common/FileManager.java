package me.kall.lootstories.common;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.records.Info;
import me.kall.lootstories.common.records.Story;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileManager {
    public static void loadStories() {
        Path modConfigDir = FMLPaths.CONFIGDIR.get().resolve(LootStories.MOD_ID);

        try {
            if (!Files.exists(modConfigDir)) FileManager.extractJar("assets/lootstories/stories/", modConfigDir, false);

            try (Stream<Path> langDirs = Files.list(modConfigDir)) {
                langDirs.filter(Files::isDirectory).forEach(langDir -> readTexts(langDir, LootStories.STORY_MANAGER.storiesByLang));
            }

        } catch (IOException e) {
            LootStories.LOGGER.error("Error loading stories", e);
        }
    }

    public static void loadReadme() {
        if (LootStories.CONFIG_INSTANCE.available()) {
            try {
                extractJar("assets/lootstories/readme", FMLPaths.CONFIGDIR.get(), true);
            } catch (IOException ioException) {
                LootStories.LOGGER.error("Error loading readme files", ioException);
            }
        }
    }

    private static void extractJar(@NotNull String sourcePath, Path targetDir, boolean override) throws IOException {
        Files.createDirectories(targetDir);

        Path jar = FMLLoader.getLoadingModList().getModFileById(LootStories.MOD_ID).getFile().getFilePath();

        try (ZipFile jarFile = new ZipFile(jar.toFile())) {
            Enumeration<? extends ZipEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                ZipEntry entry = jarEntries.nextElement();
                String entryName = entry.getName();

                if (!entryName.startsWith(sourcePath) || entry.isDirectory()) continue;

                String relativePath = entryName.substring(sourcePath.length());
                Path targetFile = targetDir.resolve(relativePath);
                Files.createDirectories(targetFile.getParent());

                if (Files.exists(targetFile) && !override) continue;

                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void readTexts(@NotNull Path langDir, @NotNull Map<String, List<Story>> map) {
        String lang = langDir.getFileName().toString();
        try (Stream<Path> storyFiles = Files.list(langDir)) {
            List<Story> stories = storyFiles
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(FileManager::storyCreation)
                    .filter(Objects::nonNull)
                    .toList();
            map.put(lang, stories);
        } catch (IOException e) {
            LootStories.LOGGER.error("Error reading stories from lang dir: {}", langDir, e);
        }
    }

    private static @Nullable Story storyCreation(Path txtFile) {
        try {
            return new Story(Info.parseFile(txtFile), Files.readString(txtFile), txtFile.getFileName().toString());
        } catch (IOException e) {
            LootStories.LOGGER.error("Error reading story file: {}", txtFile, e);
            return null;
        }
    }
}
