package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.data.Story;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin extends Screen {
    @Shadow @Final protected static int IMAGE_WIDTH;
    @Shadow private int currentPage;
    @Shadow private BookViewScreen.BookAccess bookAccess;
    @Shadow @Final protected static int TEXT_WIDTH;
    @Shadow @Final protected static int TEXT_HEIGHT;

    @Shadow @Nullable public abstract Style getClickedComponentStyleAt(double mouseX, double mouseY);

    @Shadow private PageButton forwardButton;
    @Shadow private PageButton backButton;
    @Shadow @Final public static ResourceLocation BOOK_LOCATION;
    @Unique private Story loot$story = null;
    @Unique private List<List<FormattedCharSequence>> story$pages = null;

    protected BookViewScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!(bookAccess instanceof BookViewScreen.WrittenBookAccess)) {
            this.loot$story = null;
            this.story$pages = null;
            return;
        }
        loot$updateButtonVisibility();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        ItemStack itemStack = minecraft.player.getMainHandItem();
        CompoundTag tag = itemStack.getTag();

        if (itemStack.isEmpty() || tag == null || !tag.getBoolean(LootStories.MOD_ID)) return;

        ci.cancel();

        if (this.loot$story == null) {
            ResourceLocation key = ResourceLocation.tryParse(tag.getList("pages", Tag.TAG_STRING).getString(0));
            if (key == null) return;

            String lang = minecraft.getLanguageManager().getSelected();
            List<Story> langStories = LootStories.STORIES.getOrDefault(lang, LootStories.STORIES.getOrDefault("en_us", List.of()));
            Story story = LootStories.KEY_MANAGER.getStory(key);

            if (!langStories.contains(story)) {
                Story copy = story;
                story = langStories.stream()
                        .filter(s -> s.info().title().equals(copy.info().title()))
                        .findFirst()
                        .orElse(story);
            }

            this.loot$story = story;
            this.story$pages = Story.splitStory(minecraft.font, this.loot$story, TEXT_WIDTH, TEXT_HEIGHT);
        }


        this.renderBackground(guiGraphics);
        int i = (this.width - 192) / 2;
        guiGraphics.blit(BOOK_LOCATION, i, 2, 0, 0, 192, 192);

        if (currentPage >= this.loot$story.pageCount) currentPage = this.loot$story.pageCount - 1;
        if (currentPage < 0) currentPage = 0;

        List<FormattedCharSequence> pageLines = currentPage < story$pages.size() ? story$pages.get(currentPage) : List.of();

        for (int pageLine = 0; pageLine < pageLines.size(); pageLine++) {
            guiGraphics.drawString(minecraft.font, pageLines.get(pageLine), i + 36, 32 + pageLine * 9, 0, false);
        }

        String pageStr = String.format("%d / %d", currentPage + 1, this.loot$story.pageCount);
        int pageWidth = minecraft.font.width(pageStr);
        guiGraphics.drawString(minecraft.font, pageStr, i + IMAGE_WIDTH - pageWidth - 44, 18, 0, false);

        Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Inject(method = "getNumPages", at = @At("HEAD"), cancellable = true)
    private void onPage(CallbackInfoReturnable<Integer> cir) {
        if (this.loot$story == null || this.story$pages == null) return;
        cir.setReturnValue(this.loot$story.pageCount);
    }

    @Unique
    private void loot$updateButtonVisibility() {
        if (this.forwardButton != null && this.backButton != null && story$pages != null) {
            int totalPages = this.loot$story.pageCount;
            this.forwardButton.visible = currentPage < totalPages - 1;
            this.backButton.visible = currentPage > 0;
        }
    }
}
