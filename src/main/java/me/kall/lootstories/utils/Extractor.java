package me.kall.lootstories.utils;

import me.kall.lootstories.LootStories;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Extractor {
    public static void extractJar(String modId, String sourcePath, Path targetDir, boolean overwrite) {
        try {
            Files.createDirectories(targetDir);

            ModFileInfo modFile = FMLLoader.getLoadingModList().getModFileById(modId);
            Path jarPath = modFile.getFile().getFilePath();
            try (ZipFile zip = new ZipFile(jarPath.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (!entryName.startsWith(sourcePath)) continue;
                    if (entry.isDirectory()) continue;

                    Path targetFile = targetDir.resolve(Path.of(entryName).getFileName());
                    if (Files.exists(targetFile) && !overwrite) continue;

                    try (InputStream in = zip.getInputStream(entry)) {
                        Files.copy(in, targetFile);
                        LootStories.LOGGER.info("[LootStories] Extracted {} to {}", entryName, targetFile);
                    } catch (Exception e) {
                        LootStories.LOGGER.warn("[LootStories] Failed to extract {}: {}", entryName, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            LootStories.LOGGER.warn("[LootStories] Failed to extract resources from mod jar", e);
        }
    }
}
