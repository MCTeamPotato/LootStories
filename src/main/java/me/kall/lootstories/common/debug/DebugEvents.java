package me.kall.lootstories.common.debug;

import me.kall.lootstories.LootStories;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = LootStories.MOD_ID)
public class DebugEvents {
    private static final boolean enabled = false;

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!enabled) return;
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            player.setGameMode(GameType.SPECTATOR);
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1));
            BlockPos pos = player.getLevel().findNearestMapFeature(StructureFeature.STRONGHOLD, player.blockPosition(), 114514, false);
            if (pos != null) {
                player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }
}
