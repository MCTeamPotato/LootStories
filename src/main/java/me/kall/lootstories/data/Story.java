package me.kall.lootstories.data;

import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Story {
    private final Info info;
    private final String content;

    private List<List<FormattedText>> pages;
    public int pageCount;

    public Story(Info info, String content) {
        this.info = info;
        this.content = content;
        pageCount = countPages(this.content);
    }

    public void setPages(List<List<FormattedText>> pages) {
        this.pages = pages;
    }

    public List<List<FormattedText>> pages() {
        return this.pages;
    }

    public Info info() {
        return info;
    }

    public String content() {
        return content;
    }

    public static int countPages(@NotNull String story) {
        int pages = 0;
        int pageLength = 128;
        int start = 0;

        while (start < story.length()) {
            int end = Math.min(start + pageLength, story.length());
            pages++;
            start = end;
        }

        return pages;
    }
}
