package com.example.memeheroes.network;

import com.example.memeheroes.item.ModItems;
import com.example.memeheroes.meme.MemeType;
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
            if (memeIndex < 1 || memeIndex > 5) return;

            MemeType newMeme = MemeType.byId(memeIndex);
            if (newMeme == null) return;

            // 换梗道具触发：检查并添加冷却（300s = 6000t）
            if (fromItem) {
                if (sp.getCooldowns().isOnCooldown(ModItems.CHANGE_MEME.get())) return;
                sp.getCooldowns().addCooldown(ModItems.CHANGE_MEME.get(), 6000);
            }

            // 清除旧梗物品
            int oldId = sp.getPersistentData().getInt("selected_meme");
            if (oldId != 0) {
                MemeType oldMeme = MemeType.byId(oldId);
                if (oldMeme != null) {
                    clearMemeItems(sp, oldMeme);
                }
            }

            // 记录新梗
            sp.getPersistentData().putInt("selected_meme", memeIndex);

            // 发放新梗物品
            giveMemeItems(sp, newMeme);
        });
        ctx.setPacketHandled(true);
    }

    private static void clearMemeItems(Player player, MemeType meme) {
        List<Item> items = meme.getItems();
        Inventory inv = player.getInventory();
        // 遍历全部槽位（主背包36 + 盔甲4 + 副手1），确保副手物品也被清除
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

    private static void giveMemeItems(Player player, MemeType meme) {
        for (Item item : meme.getItems()) {
            ItemStack stack = new ItemStack(item);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
