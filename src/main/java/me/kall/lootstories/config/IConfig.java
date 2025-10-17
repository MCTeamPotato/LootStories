package me.kall.lootstories.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.kall.lootstories.data.Story;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.Map;

public interface IConfig {
    int possibility();

    Object2IntMap<String> storyWeight();

    Map<ResourceLocation, ShufflingList<Story>> bindStories();

    void initStoryWeight();

    void initBindStories();
}
