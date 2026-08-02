package com.example.memeheroes.network;

import com.example.memeheroes.client.ClientKillLeaderboardData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CKillLeaderboardPacket {
    private final List<String> names;
    private final List<Integer> kills;

    public S2CKillLeaderboardPacket(List<String> names, List<Integer> kills) {
        this.names = names;
        this.kills = kills;
    }

    public static void encode(S2CKillLeaderboardPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.names.size());
        for (int i = 0; i < msg.names.size(); i++) {
            buf.writeUtf(msg.names.get(i));
            buf.writeInt(msg.kills.get(i));
        }
    }

    public static S2CKillLeaderboardPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<String> names = new ArrayList<>(size);
        List<Integer> kills = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            names.add(buf.readUtf(64));
            kills.add(buf.readInt());
        }
        return new S2CKillLeaderboardPacket(names, kills);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        ctxSup.get().enqueueWork(() -> ClientKillLeaderboardData.set(names, kills));
        ctxSup.get().setPacketHandled(true);
    }
}
