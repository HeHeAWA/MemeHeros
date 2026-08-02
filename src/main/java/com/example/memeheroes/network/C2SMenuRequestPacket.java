package com.example.memeheroes.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class C2SMenuRequestPacket {
    public C2SMenuRequestPacket() {
    }

    public static void encode(C2SMenuRequestPacket msg, FriendlyByteBuf buf) {
    }

    public static C2SMenuRequestPacket decode(FriendlyByteBuf buf) {
        return new C2SMenuRequestPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sp = ctx.getSender();
            if (sp == null) return;
            int kills = sp.getPersistentData().getInt("kill_count");
            int memeId = sp.getPersistentData().getInt("selected_meme");
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp),
                    new S2CMenuInfoPacket(kills, memeId));
        });
        ctx.setPacketHandled(true);
    }
}
