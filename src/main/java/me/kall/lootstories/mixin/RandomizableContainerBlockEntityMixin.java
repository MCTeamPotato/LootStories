package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.common.records.Story;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;


@Mixin(RandomizableContainer.class)
public interface RandomizableContainerBlockEntityMixin extends Container {
    @Shadow @Nullable ResourceKey<LootTable> getLootTable();

    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V", shift = At.Shift.AFTER))
    private void onLootFill(Player player, CallbackInfo ci) {
        if (LootStories.STORY_MANAGER.canAddBook()) {
            ItemStack book = Items.WRITTEN_BOOK.getDefaultInstance();
            ResourceKey<LootTable> tableKey = this.getLootTable();
            ResourceLocation lootTable = tableKey == null ? null : tableKey.location();
            Story story = LootStories.STORY_MANAGER.getStoryForGen(lootTable);

            book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough(story.info().title()), story.info().author(), 0, List.of(), true));
            book.set(DataComponents.CUSTOM_DATA, CustomData.of(LootStories.STORY_MANAGER.createStoryTag(lootTable)));

            for (int i = 0; i < this.getContainerSize(); i++) {
                if (this.getItem(i).isEmpty()) {
                    this.setItem(i, book);
                    break;
                }
            }
        }
    }
}
