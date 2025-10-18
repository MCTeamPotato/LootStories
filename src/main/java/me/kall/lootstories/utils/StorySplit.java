package me.kall.lootstories.utils;

import me.kall.lootstories.data.Story;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StorySplit {
    public static @NotNull List<List<FormattedCharSequence>> splitStory(@NotNull Font font, Component text, int textWidth, int textHeight) {
        List<FormattedCharSequence> lines = font.split(text, textWidth);
        int lineHeight = 9;
        int linesPerPage = textHeight / lineHeight;

        List<List<FormattedCharSequence>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += linesPerPage) {
            int end = Math.min(i + linesPerPage, lines.size());
            pages.add(new ArrayList<>(lines.subList(i, end)));
        }
        return pages;
    }

    public static List<List<FormattedCharSequence>> splitStory(Font font, @NotNull Story story, int textWidth, int textHeight) {
        if (story.pages() != null) return story.pages();
        List<List<FormattedCharSequence>> pages = splitStory(font, Component.literal(story.content()), textWidth, textHeight);
        story.setPages(pages);
        return pages;
    }
}
