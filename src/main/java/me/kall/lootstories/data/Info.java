package me.kall.lootstories.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Info(String title, String author) {
    public static @NotNull Info parse(@Nullable String filename) {
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
}
