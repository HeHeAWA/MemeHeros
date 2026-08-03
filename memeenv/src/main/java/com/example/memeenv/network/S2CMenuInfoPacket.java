package com.example.memeenv.network;

import com.example.memeenv.client.ClientMenuData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CMenuInfoPacket {
    private final int killCount;
    private final int selectedMemeId;

    public S2CMenuInfoPacket(int killCount, int selectedMemeId) {
        this.killCount = killCount;
        this.selectedMemeId = selectedMemeId;
    }

    public static void encode(S2CMenuInfoPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.killCount);
        buf.writeInt(msg.selectedMemeId);
    }

    public static S2CMenuInfoPacket decode(FriendlyByteBuf buf) {
        return new S2CMenuInfoPacket(buf.readInt(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        ctxSup.get().enqueueWork(() -> {
            ClientMenuData.set(killCount, selectedMemeId);
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.example.memeenv.client.ClientMemeScreenHandler.openMenuScreen());
        });
        ctxSup.get().setPacketHandled(true);
    }
}
