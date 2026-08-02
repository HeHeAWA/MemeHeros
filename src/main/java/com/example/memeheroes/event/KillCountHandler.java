package com.example.memeheroes.event;

import com.example.memeheroes.network.ModMessages;
import com.example.memeheroes.network.S2CKillLeaderboardPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "memeheroes", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillCountHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) return;
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        // 击杀者必须是玩家且不能是自杀
        if (attacker instanceof ServerPlayer killer && killer != victim) {
            int kills = killer.getPersistentData().getInt("kill_count");
            killer.getPersistentData().putInt("kill_count", kills + 1);
            MinecraftServer server = killer.getServer();
            if (server != null) {
                broadcastLeaderboard(server);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        MinecraftServer server = sp.getServer();
        if (server != null) {
            sendLeaderboardTo(server, sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (event.player.tickCount % 400 != 0) return; // 每 20 秒全量同步一次
        MinecraftServer server = event.player.getServer();
        if (server != null) {
            broadcastLeaderboard(server);
        }
    }

    private static S2CKillLeaderboardPacket buildLeaderboard(MinecraftServer server) {
        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        // 按击杀数降序排序
        players.sort((a, b) -> Integer.compare(
                b.getPersistentData().getInt("kill_count"),
                a.getPersistentData().getInt("kill_count")));

        List<String> names = new ArrayList<>(players.size());
        List<Integer> kills = new ArrayList<>(players.size());
        for (ServerPlayer p : players) {
            names.add(p.getName().getString());
            kills.add(p.getPersistentData().getInt("kill_count"));
        }
        return new S2CKillLeaderboardPacket(names, kills);
    }

    private static void broadcastLeaderboard(MinecraftServer server) {
        ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), buildLeaderboard(server));
    }

    private static void sendLeaderboardTo(MinecraftServer server, ServerPlayer target) {
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> target),
                buildLeaderboard(server));
    }
}
