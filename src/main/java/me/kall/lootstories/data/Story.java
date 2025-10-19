package me.kall.lootstories.data;

import net.minecraft.network.chat.FormattedText;

import java.util.List;

public class Story {
    private final Info info;
    private final String content;

    private List<List<FormattedText>> pages;

    public Story(Info info, String content) {
        this.info = info;
        this.content = content;
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
}
