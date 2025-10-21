package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.access.IBookAccess;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BookViewScreen.BookAccess.class)
public class WrittenBookAccessMixin implements IBookAccess {
    @Unique private @Nullable List<FormattedText> story$pages;

    @Inject(method = "fromItem", at = @At("RETURN"))
    private static void init(ItemStack book, CallbackInfoReturnable<BookViewScreen.BookAccess> cir) {
        IBookAccess access = (IBookAccess) (Object) cir.getReturnValue();
        if (access == null) return;
        LootStories.STORY_MANAGER.loadBookPages(book, access);
    }

    @Override
    public @Nullable List<FormattedText> story$pages() {
        return this.story$pages;
    }

    @Override
    public void story$loadPages(List<FormattedText> pages) {
        this.story$pages = pages;
    }

    @Inject(method = "getPageCount", at = @At("HEAD"), cancellable = true)
    private void onPageCount(CallbackInfoReturnable<Integer> cir) {
        if (this.story$pages == null) return;
        cir.setReturnValue(this.story$pages.size());
    }

    @Inject(method = "getPage", at = @At("HEAD"), cancellable = true)
    private void onGetPage(int index, CallbackInfoReturnable<FormattedText> cir) {
        if (this.story$pages == null) return;
        if (index < 0 || index >= this.story$pages.size()) return;
        FormattedText page = this.story$pages.get(index);
        if (page == null) return;
        cir.setReturnValue(page);
    }
}
