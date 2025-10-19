package me.kall.lootstories.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public record Info(String title, String author) {
    public static @NotNull Info parse(@Nullable Path path) {
        if (path == null) return new Info("", "unknown");

        if (!Files.exists(path) || Files.isDirectory(path)) return new Info("", "unknown");

        try (Stream<String> lines = Files.lines(path)) {
            String firstLine = lines.findFirst().orElse("").trim();

            if (firstLine.isEmpty()) return new Info("", "unknown");

            return parseLine(firstLine);
        } catch (IOException e) {
            return new Info("", "unknown");
        }
    }

    private static @NotNull Info parseLine(@NotNull String line) {
        int byIndex = line.lastIndexOf("by");
        if (byIndex > 0) {
            String title = line.substring(0, byIndex).trim();
            String author = line.substring(byIndex + 2).trim();
            if (author.isEmpty()) author = "unknown";
            if (title.isEmpty()) title = "Untitled";
            return new Info(title, author);
        }

        return new Info(line, "unknown");
    }
}