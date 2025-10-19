package me.kall.lootstories.utils;

import me.kall.lootstories.data.Story;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StorySplit {
    public static @NotNull List<List<FormattedText>> splitStory(@NotNull Font font, Component text, int textWidth, int textHeight) {
        List<FormattedText> lines = font.getSplitter().splitLines(text, textWidth, Style.EMPTY);
        int lineHeight = 9;
        int linesPerPage = textHeight / lineHeight;

        List<List<FormattedText>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += linesPerPage) {
            int end = Math.min(i + linesPerPage, lines.size());
            pages.add(new ArrayList<>(lines.subList(i, end)));
        }
        return pages;
    }

    public static List<List<FormattedText>> splitStory(Font font, @NotNull Story story, int textWidth, int textHeight) {
        if (story.pages() != null) return story.pages();
        List<List<FormattedText>> pages = splitStory(font, Component.literal(story.content()), textWidth, textHeight);
        story.setPages(pages);
        return pages;
    }
}
