package com.example.memeenv.network;

import com.example.memeenv.api.MemeBridge;
import com.example.memeenv.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class C2SMemeSelectPacket {
    private final int memeIndex;
    private final boolean fromItem;

    public C2SMemeSelectPacket(int memeIndex, boolean fromItem) {
        this.memeIndex = memeIndex;
        this.fromItem = fromItem;
    }

    public static void encode(C2SMemeSelectPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.memeIndex);
        buf.writeBoolean(msg.fromItem);
    }

    public static C2SMemeSelectPacket decode(FriendlyByteBuf buf) {
        return new C2SMemeSelectPacket(buf.readInt(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sp = ctx.getSender();
            if (sp == null) return;
            MemeBridge.MemeEntry newMeme = MemeBridge.byId(memeIndex);
            if (newMeme == null) return;

            if (fromItem) {
                if (sp.getCooldowns().isOnCooldown(ModItems.CHANGE_MEME.get())) return;
                sp.getCooldowns().addCooldown(ModItems.CHANGE_MEME.get(), 6000);
            }

            int oldId = sp.getPersistentData().getInt("selected_meme");
            if (oldId != 0) {
                MemeBridge.MemeEntry oldEntry = MemeBridge.byId(oldId);
                if (oldEntry != null) {
                    clearMemeItems(sp, oldEntry);
                }
            }

            sp.getPersistentData().putInt("selected_meme", memeIndex);
            giveMemeItems(sp, newMeme);
        });
        ctx.setPacketHandled(true);
    }

    private static void clearMemeItems(Player player, MemeBridge.MemeEntry meme) {
        List<Item> items = meme.getItems();
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                for (Item item : items) {
                    if (stack.is(item)) {
                        inv.setItem(i, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
        inv.setChanged();
    }

    private static void giveMemeItems(Player player, MemeBridge.MemeEntry meme) {
        for (Item item : meme.getItems()) {
            ItemStack stack = new ItemStack(item);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
