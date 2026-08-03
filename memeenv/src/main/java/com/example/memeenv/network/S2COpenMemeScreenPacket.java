package com.example.memeenv.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2COpenMemeScreenPacket {
    public S2COpenMemeScreenPacket() {
    }

    public static void encode(S2COpenMemeScreenPacket msg, FriendlyByteBuf buf) {
    }

    public static S2COpenMemeScreenPacket decode(FriendlyByteBuf buf) {
        return new S2COpenMemeScreenPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        ctxSup.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> com.example.memeenv.client.ClientMemeScreenHandler.openLoginScreen())
        );
        ctxSup.get().setPacketHandled(true);
    }
}
