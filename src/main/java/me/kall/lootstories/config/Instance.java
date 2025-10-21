package me.kall.lootstories.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Instance {
    private final IConfig config;

    public Instance() {
        this.config = FMLLoader.getLoadingModList().getModFileById("jsonate") == null ? null : new StoryConfig();
    }

    public boolean available() {
        return this.config != null;
    }

    public int bookLootChance() {
        return !this.available() ? 100 : this.config.bookLootChance();
    }

    public int getWeight(String title) {
        return !this.available() ? 10 : this.config.storyWeights().getOrDefault(title, 10);
    }

    public Map<ResourceLocation, Set<String>> storiesToBind() {
        return this.available() ? this.config.boundStoryTitles() : Collections.emptyMap();
    }
}
