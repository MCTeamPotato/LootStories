package me.kall.lootstories;

import me.kall.lootstories.common.FileManager;
import me.kall.lootstories.common.StoryManager;
import me.kall.lootstories.config.Instance;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LootStories.MOD_ID)
public final class LootStories {
    public static final String MOD_ID = "lootstories";
    public static final Logger LOGGER = LogManager.getLogger("LootStories");

    public static final Instance CONFIG_INSTANCE = new Instance();
    public static final StoryManager STORY_MANAGER = new StoryManager();

    static {
        FileManager.loadStories();
        FileManager.loadReadme();
        STORY_MANAGER.loadStories();
        STORY_MANAGER.bindStories();
    }
}
