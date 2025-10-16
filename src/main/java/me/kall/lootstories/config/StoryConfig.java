package me.kall.lootstories.config;

import me.kall.jsonate.api.JsonConfig;
import me.kall.lootstories.LootStories;

public class StoryConfig implements IConfig {
    private final JsonConfig storyConfig = JsonConfig.create(LootStories.MOD_ID, "1")
            .put("BookLootPossibility(%)", 60)
            .initialize();
    private final int possibility = storyConfig.getInt("BookLootPossibility(%)");

    @Override
    public int possibility() {
        return this.possibility;
    }
}
