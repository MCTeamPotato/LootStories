package me.kall.lootstories.common.util;

import me.kall.lootstories.common.records.Story;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Splitter {
    public static List<FormattedText> splitStory(Font font, @NotNull Story story, int textWidth, int textHeight) {
        if (story.pages() != null) return story.pages();
        var lines = font.getSplitter().splitLines(Component.literal(story.content()), textWidth, Style.EMPTY);
        List<FormattedText> pages = new ArrayList<>();
        int linesPerPage = textHeight / font.lineHeight;
        for (int i = 0; i < lines.size(); i += linesPerPage) {
            pages.add(FormattedText.composite(lines.subList(i, Math.min(i + linesPerPage, lines.size()))));
        }

        story.loadPages(pages);
        return pages;
    }
}
