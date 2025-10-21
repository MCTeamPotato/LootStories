package me.kall.lootstories.common.records;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@SuppressWarnings("ClassCanBeRecord")
public class Info {
    private final String title;
    private final String author;
    private static final Info UNKNOWN = new Info("Untitled", "unknown");

    public Info(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public static @NotNull Info parseFile(Path path) {
        if (path == null || !Files.exists(path) || Files.isDirectory(path)) return UNKNOWN;
        try (Stream<String> lines = Files.lines(path)){
            String firstLine = lines.findFirst().orElse("").trim();
            if (firstLine.isEmpty()) return UNKNOWN;
            int byIndex = firstLine.lastIndexOf("by");
            if (byIndex > 0) {
                String title = firstLine.substring(0, byIndex).trim();
                String author = firstLine.substring(byIndex + 2).trim();
                if (author.isEmpty()) author = "unknown";
                if (title.isEmpty()) title = "Untitled";
                return new Info(title, author);
            }

            return UNKNOWN;
        } catch (IOException ioException) {
            return UNKNOWN;
        }
    }
}
