package me.kall.lootstories.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public interface IConfig {
    int possibility();
    Object2IntMap<String> storyWeight();
}
