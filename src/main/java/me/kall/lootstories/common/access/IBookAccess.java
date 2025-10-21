package me.kall.lootstories.common.access;

import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IBookAccess {
    @Nullable List<FormattedText> story$pages();
    void story$loadPages(List<FormattedText> pages);
}
