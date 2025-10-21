package me.kall.lootstories.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.records.Story;
import me.kall.lootstories.common.util.Lang;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin {
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void titleGet(@NotNull ItemStack book, CallbackInfoReturnable<Component> cir) {
        CustomData customData = book.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;
        CompoundTag tag = customData.copyTag();
        if (!tag.getBoolean(LootStories.MOD_ID)) return;

        Story story = null;
        try {
            story = LootStories.STORY_MANAGER.storiesByFile.get(tag.getList("pages", Tag.TAG_STRING).getString(0)).get(Lang.getLang());
        } catch (Throwable ignored) {}

        if (story == null) return;
        cir.setReturnValue(Component.literal(story.info().title()));
    }

    @WrapOperation(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/WrittenBookContent;author()Ljava/lang/String;"))
    private String authorGet(WrittenBookContent instance, Operation<String> original, @Local(ordinal = 0, argsOnly = true) ItemStack book) {
        CustomData customData = book.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return original.call(instance);
        CompoundTag tag = customData.copyTag();
        if (!tag.getBoolean(LootStories.MOD_ID)) return original.call(instance);
        Story story = null;
        try {
            story = LootStories.STORY_MANAGER.storiesByFile.get(tag.getList("pages", Tag.TAG_STRING).getString(0)).get(Lang.getLang());
        } catch (Throwable ignored) {}

        if (story == null) return original.call(instance);
        return story.info().author();
    }
}
