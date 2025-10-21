package me.kall.lootstories.common.records;

import net.minecraft.network.chat.FormattedText;

import java.util.List;

public class Story {
    private final Info info;
    private final String content;
    private final String basedFile;
    private List<FormattedText> pages;

    public Story(Info info, String content, String basedFile) {
        this.info = info;
        this.content = content;
        this.basedFile = basedFile;
    }

    public void loadPages(List<FormattedText> pages) {
        this.pages = pages;
    }

    public List<FormattedText> pages() {
        return this.pages;
    }

    public Info info() {
        return this.info;
    }

    public String content() {
        return this.content;
    }

    public String basedFile() {
        return this.basedFile;
    }
}
