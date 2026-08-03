package com.example.memeenv.network;

import com.example.memeenv.MemeEnv;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MemeEnv.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.messageBuilder(C2SMemeSelectPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SMemeSelectPacket::encode)
                .decoder(C2SMemeSelectPacket::decode)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx))
                .add();

        INSTANCE.messageBuilder(S2COpenMemeScreenPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2COpenMemeScreenPacket::encode)
                .decoder(S2COpenMemeScreenPacket::decode)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx))
                .add();

        INSTANCE.messageBuilder(S2CKillLeaderboardPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CKillLeaderboardPacket::encode)
                .decoder(S2CKillLeaderboardPacket::decode)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx))
                .add();

        INSTANCE.messageBuilder(C2SMenuRequestPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SMenuRequestPacket::encode)
                .decoder(C2SMenuRequestPacket::decode)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx))
                .add();

        INSTANCE.messageBuilder(S2CMenuInfoPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CMenuInfoPacket::encode)
                .decoder(S2CMenuInfoPacket::decode)
                .consumerMainThread((msg, ctx) -> msg.handle(ctx))
                .add();

        // 注册完毕后冻结 MemeBridge 注册表
        com.example.memeenv.api.MemeBridge.freeze();
    }
}
