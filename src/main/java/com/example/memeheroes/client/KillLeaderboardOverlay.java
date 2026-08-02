package com.example.memeheroes.client;

import com.example.memeheroes.MemeHeroes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MemeHeroes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KillLeaderboardOverlay {

    private static final int MAX_ROWS = 10;
    private static final int PANEL_W = 140;
    private static final int MARGIN = 5;
    private static final int LINE_H = 11;
    private static final int TITLE_H = 14;

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        if (ClientKillLeaderboardData.isEmpty()) return;

        GuiGraphics gg = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;

        List<String> names = ClientKillLeaderboardData.getNames();
        List<Integer> kills = ClientKillLeaderboardData.getKills();

        int rows = Math.min(names.size(), MAX_ROWS);
        int panelH = TITLE_H + rows * LINE_H + 4;

        int screenWidth = gg.guiWidth();
        int x = screenWidth - PANEL_W - MARGIN;
        int y = MARGIN;

        // 半透明背景
        gg.fill(x, y, x + PANEL_W, y + panelH, 0x80000000);
        // 边框
        gg.fill(x, y, x + PANEL_W, y + 1, 0xFF55FF55);
        gg.fill(x, y + panelH - 1, x + PANEL_W, y + panelH, 0xFF55FF55);

        // 标题
        gg.drawString(font, Component.translatable("memeheroes.leaderboard.title"), x + 5, y + 3, 0xFFFFAA00);

        // 条目
        for (int i = 0; i < rows; i++) {
            String rawName = names.get(i);
            // 截断过长名字
            String name = rawName.length() > 10 ? rawName.substring(0, 10) : rawName;
            String line = (i + 1) + ". " + name + " : " + kills.get(i);
            // 第一名金色，其余白色
            int color = i == 0 ? 0xFFFFAA00 : 0xFFFFFFFF;
            gg.drawString(font, line, x + 5, y + TITLE_H + i * LINE_H, color);
        }
    }
}
