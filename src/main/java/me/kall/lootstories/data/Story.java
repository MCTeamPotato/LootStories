package me.kall.lootstories.data;

import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Story {
    private final Info info;
    private final String content;

    private List<List<FormattedCharSequence>> pages;
    public int pageCount;

    public Story(Info info, String content) {
        this.info = info;
        this.content = content;
        pageCount = countPages(this.content);
    }

    public void setPages(List<List<FormattedCharSequence>> pages) {
        this.pages = pages;
    }

    public List<List<FormattedCharSequence>> pages() {
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
