package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.LootStories.StoryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BaseContainerBlockEntity {
    @Shadow protected abstract NonNullList<ItemStack> getItems();
    @Shadow public abstract @NotNull ItemStack getItem(int slot);

    protected RandomizableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V", shift = At.Shift.AFTER))
    private void onLoadLoots(Player player, CallbackInfo ci) {
        if (LootStories.mayGen() && !LootStories.STORIES.isEmpty()) {
            ItemStack stack = Items.WRITTEN_BOOK.getDefaultInstance();

            CompoundTag bookTag = new CompoundTag();

            int idx = ThreadLocalRandom.current().nextInt(LootStories.STORIES.size());
            StoryEntry entry = LootStories.STORIES.get(idx);
            String story = entry.content();
            ListTag pages = LootStories.createPagesFromStory(story);
            bookTag.put("pages", pages);

            String title = story.contains("\n") ? story.substring(0, story.indexOf('\n')) : story;
            bookTag.putString("title", title);

            String authorFromFile = entry.author();
            if (authorFromFile == null || authorFromFile.isEmpty()) {
                bookTag.putString("author", "unknown");
            } else {
                bookTag.putString("author", authorFromFile);
            }

            bookTag.putBoolean("resolved", true);

            stack.setTag(bookTag);

            for (int i = 0; i < this.getItems().size(); i++) {
                if (this.getItem(i).isEmpty()) {
                    this.setItem(i, stack);
                    break;
                }
            }
        }
    }
}
