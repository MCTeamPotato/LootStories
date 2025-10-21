package me.kall.lootstories.mixin;

import me.kall.lootstories.LootStories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BaseContainerBlockEntity {
    @Shadow @Nullable protected ResourceLocation lootTable;
    @Shadow protected abstract NonNullList<ItemStack> getItems();
    @Shadow public abstract @NotNull ItemStack getItem(int slot);

    protected RandomizableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V", shift = At.Shift.AFTER))
    private void onLootFill(Player player, CallbackInfo ci) {
        if (LootStories.STORY_MANAGER.canAddBook()) {
            ItemStack book = Items.WRITTEN_BOOK.getDefaultInstance();
            book.setTag(LootStories.STORY_MANAGER.createStoryTag(this.lootTable));

            for (int i = 0; i < this.getItems().size(); i++) {
                if (this.getItem(i).isEmpty()) {
                    this.setItem(i, book);
                    break;
                }
            }
        }
    }
}
