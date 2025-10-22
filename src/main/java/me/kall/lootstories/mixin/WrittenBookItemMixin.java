package me.kall.lootstories.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.records.Story;
import me.kall.lootstories.common.util.Lang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin {
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void titleGet(@NotNull ItemStack stack, CallbackInfoReturnable<Component> cir) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean(LootStories.MOD_ID)) return;

        Story story = null;
        try {
            story = LootStories.STORY_MANAGER.storiesByFile.get(tag.getList("pages", Tag.TAG_STRING).getString(0)).get(Lang.getLang());
        } catch (Throwable ignored) {}

        if (story == null) return;
        cir.setReturnValue(new TextComponent(story.info().title()));
    }

    @WrapOperation(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getString(Ljava/lang/String;)Ljava/lang/String;"))
    private String authorGet(@NotNull CompoundTag tag, String key, Operation<String> original) {
        if (!tag.getBoolean(LootStories.MOD_ID)) return original.call(tag, key);
        Story story = null;
        try {
            story = LootStories.STORY_MANAGER.storiesByFile.get(tag.getList("pages", Tag.TAG_STRING).getString(0)).get(Lang.getLang());
        } catch (Throwable ignored) {}

        if (story == null) return original.call(tag, key);
        return story.info().author();
    }
}
