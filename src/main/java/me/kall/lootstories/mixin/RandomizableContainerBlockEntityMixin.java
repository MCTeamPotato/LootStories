package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import me.kall.lootstories.data.Info;
import me.kall.lootstories.data.Story;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BaseContainerBlockEntity {
    @Shadow protected abstract NonNullList<ItemStack> getItems();
    @Shadow public abstract @NotNull ItemStack getItem(int slot);
    @Shadow @Nullable protected ResourceLocation lootTable;

    protected RandomizableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V", shift = At.Shift.AFTER))
    private void onLoadLoots(Player player, CallbackInfo ci) {
        if (story$mayGen() && LootStories.STORIES.iterator().hasNext()) {
            ItemStack stack = Items.WRITTEN_BOOK.getDefaultInstance();
            stack.setTag(this.story$fillBook());
            for (int i = 0; i < this.getItems().size(); i++) {
                if (this.getItem(i).isEmpty()) {
                    this.setItem(i, stack);
                    break;
                }
            }
        }
    }

    @Unique
    private @NotNull CompoundTag story$fillBook() {
        CompoundTag bookTag = new CompoundTag();

        Story story = story$selectStory();
        ListTag pages = this.story$createPages(story.content());
        bookTag.put("pages", pages);

        Info info = story.info();
        bookTag.putString("title", info.title());
        String author = (info.author() == null || info.author().isEmpty()) ? "unknown" : info.author();
        bookTag.putString("author", author);

        bookTag.putBoolean("resolved", true);
        return bookTag;
    }

    @Unique
    private @NotNull ListTag story$createPages(@NotNull String story) {
        ListTag pages = new ListTag();
        int pageLength = 128;
        int start = 0;

        while (start < story.length()) {
            int end = Math.min(start + pageLength, story.length());
            pages.add(StringTag.valueOf(story.substring(start, end)));
            start = end;
        }

        return pages;
    }

    @Unique
    private Story story$selectStory() {
        if (LootStories.CONFIG == null) {
            return story$getRandom(LootStories.STORIES);
        }

        if (this.lootTable != null) {
            ShufflingList<Story> bound = LootStories.CONFIG.bindStories().get(this.lootTable);
            if (bound != null && bound.iterator().hasNext()) {
                return story$getRandom(bound);
            }
        }

        return story$getRandom(LootStories.STORIES);
    }

    @Unique
    private Story story$getRandom(@NotNull ShufflingList<Story> stories) {
        return stories.shuffle().stream().findFirst().orElseThrow();
    }

    @Unique
    private static boolean story$mayGen() {
        return ThreadLocalRandom.current().nextInt(100) < (LootStories.CONFIG == null ? 100 : LootStories.CONFIG.possibility());
    }
}
