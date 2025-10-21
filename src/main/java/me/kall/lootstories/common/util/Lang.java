package me.kall.lootstories.common.util;

import net.minecraft.client.Minecraft;

public class Lang {
    public static String getLang() {
        return Minecraft.getInstance().getLanguageManager().getSelected().getCode();
    }
}
