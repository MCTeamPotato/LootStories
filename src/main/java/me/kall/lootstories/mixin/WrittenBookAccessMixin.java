package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.data.Story;
import me.kall.lootstories.util.StorySplit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BookViewScreen.WrittenBookAccess.class)
public abstract class WrittenBookAccessMixin {
    @Unique private List<FormattedText> book$storyPages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ItemStack book, CallbackInfo ci) {
        this.story$load(book);
    }

    @Unique
    private void story$load(@NotNull ItemStack book) {
        CompoundTag tag = book.getTag();
        if (book.isEmpty() || tag == null || !tag.getBoolean(LootStories.MOD_ID)) return;

        if (this.book$storyPages == null) {
            ResourceLocation key = ResourceLocation.tryParse(tag.getList("pages", Tag.TAG_STRING).getString(0));
            if (key == null) return;

            Minecraft minecraft = Minecraft.getInstance();

            String lang = minecraft.getLanguageManager().getSelected();
            List<Story> langStories = LootStories.STORIES.getOrDefault(lang, LootStories.STORIES.getOrDefault("en_us", List.of()));
            Story story = LootStories.KEY_MANAGER.getStory(key);

            if (!langStories.contains(story)) {
                Story copy = story;
                story = langStories.stream().filter(s -> s.info().title().equals(copy.info().title())).findFirst().orElse(story);
            }

            this.book$storyPages = StorySplit.splitStory(minecraft.font, story, 114, 128)
                    .stream()
                    .map(pageList -> {
                        StringBuilder builder = new StringBuilder();
                        for (FormattedText text : pageList) {
                            builder.append(text.getString());
                        }
                        return FormattedText.of(builder.toString());
                    }).toList();
        }
    }

    @Inject(method = "getPageCount", at = @At("HEAD"), cancellable = true)
    private void onCountPage(CallbackInfoReturnable<Integer> cir) {
        if (this.book$storyPages == null) return;
        cir.setReturnValue(this.book$storyPages.size());
    }

    @Inject(method = "getPageRaw", at = @At("HEAD"), cancellable = true)
    private void onCreatePage(int index, CallbackInfoReturnable<FormattedText> cir) {
        FormattedText page = this.story$getPage(index);
        if (page == null) return;
        cir.setReturnValue(page);
    }

    @Unique
    private @Nullable FormattedText story$getPage(int index) {
        if (this.book$storyPages == null) return null;
        try {
            return this.book$storyPages.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
