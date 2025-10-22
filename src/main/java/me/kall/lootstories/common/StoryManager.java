package me.kall.lootstories.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.access.IBookAccess;
import me.kall.lootstories.common.records.Info;
import me.kall.lootstories.common.records.Story;
import me.kall.lootstories.common.util.Splitter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class StoryManager {
    public final Map<String, List<Story>> storiesByLang;
    private final ShufflingList<Story> storiesEn;
    private final Map<ResourceLocation, ShufflingList<Story>> boundStoriesEn;
    public final Map<String, Map<String, Story>> storiesByFile;

    public StoryManager() {
        this.storiesByLang = new Object2ObjectOpenHashMap<>();
        this.storiesEn = new ShufflingList<>();
        this.boundStoriesEn = new Object2ObjectOpenHashMap<>();
        this.storiesByFile = new Object2ObjectOpenHashMap<>();
    }

    public boolean canAddBook() {
        return ThreadLocalRandom.current().nextInt(100) < (!LootStories.CONFIG_INSTANCE.available() ? 100 : LootStories.CONFIG_INSTANCE.bookLootChance());
    }

    public void loadStories() {
        this.storiesByLang.get("en_us").forEach(story -> this.storiesEn.add(story, LootStories.CONFIG_INSTANCE.getWeight(story.info().title())));
        this.storiesByLang.forEach((lang, stories) -> stories.forEach(story -> storiesByFile.computeIfAbsent(String.valueOf(story.basedFile().hashCode()), key -> new Object2ObjectOpenHashMap<>()).put(lang, story)));
    }

    public void bindStories() {
        LootStories.CONFIG_INSTANCE.storiesToBind().forEach((lootTable, titles) -> {
            ShufflingList<Story> toBind = new ShufflingList<>();
            titles.forEach(title -> storiesEn.stream().filter(story -> story.info().title().equals(title)).findFirst().ifPresent(story -> toBind.add(story, LootStories.CONFIG_INSTANCE.getWeight(title))));
            if (toBind.stream().findAny().isPresent()) boundStoriesEn.put(lootTable, toBind);
        });
    }

    public CompoundTag createStoryTag(@Nullable ResourceLocation lootTable) {
        CompoundTag tag = new CompoundTag();
        Story story = getStoryForGen(lootTable);

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(String.valueOf(story.basedFile().hashCode())));
        tag.put("pages", pages);

        Info info = story.info();
        tag.putString("title", info.title());
        tag.putString("author", info.author());
        tag.putBoolean("resolved", true);
        tag.putBoolean(LootStories.MOD_ID, true);

        return tag;
    }

    public Story getStoryForGen(@Nullable ResourceLocation lootTable) {
        ShufflingList<Story> boundToGen = lootTable != null ? boundStoriesEn.get(lootTable) : null;
        if (boundToGen != null && boundToGen.stream().findAny().isPresent()) return getRandom(boundToGen);
        return getRandom(storiesEn);
    }

    private Story getRandom(@NotNull ShufflingList<Story> stories) {
        return stories.shuffle().stream().findFirst().orElseThrow();
    }

    public void loadBookPages(ItemStack book, IBookAccess access) {
        CompoundTag tag = book.getTag();
        if (book.isEmpty() || tag == null || !tag.getBoolean(LootStories.MOD_ID)) return;

        if (access.story$pages() == null) {
            String basedFile = tag.getList("pages", Tag.TAG_STRING).getString(0);

            Minecraft minecraft = Minecraft.getInstance();

            String lang = minecraft.getLanguageManager().getSelected().getCode();
            Story story = this.storiesByFile.get(basedFile).get(lang);

            access.story$loadPages(Splitter.splitStory(minecraft.font, story, 114, 128));
        }
    }
}
