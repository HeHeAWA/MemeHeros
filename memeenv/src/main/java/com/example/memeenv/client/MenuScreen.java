package com.example.memeenv.client;

import com.example.memeheroes.api.MemeBridge;
import com.example.memeenv.MemeEnv;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MenuScreen extends Screen {

    public MenuScreen() {
        super(Component.translatable(MemeEnv.MOD_ID + ".menu.title"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.close"),
                        b -> onClose())
                .bounds(this.width / 2 - 50, this.height - 35, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        renderBackground(gg);
        gg.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);

        Player player = Minecraft.getInstance().player;
        int x = this.width / 2 - 80;
        int y = 45;
        int lineH = 16;

        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.kills", ClientMenuData.getKillCount()),
                x, y, 0xFFFFFF);
        y += lineH;

        float health = player == null ? 0.0F : player.getHealth();
        float maxHealth = player == null ? 0.0F : player.getMaxHealth();
        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.health",
                        String.format("%.1f", health), String.format("%.1f", maxHealth)),
                x, y, 0xFFFFFF);
        y += lineH;

        int food = player == null ? 0 : player.getFoodData().getFoodLevel();
        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.food", food),
                x, y, 0xFFFFFF);
        y += lineH;

        int armor = player == null ? 0 : player.getArmorValue();
        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.armor", armor),
                x, y, 0xFFFFFF);
        y += lineH;

        int level = player == null ? 0 : player.experienceLevel;
        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.level", level),
                x, y, 0xFFFFAA00);
        y += lineH;

        int memeId = ClientMenuData.getSelectedMemeId();
        MemeBridge.MemeEntry entry = MemeBridge.byId(memeId);
        Component memeName = (memeId == 0 || entry == null)
                ? Component.translatable(MemeEnv.MOD_ID + ".menu.no_meme")
                : Component.translatable(entry.translationKey());
        gg.drawString(this.font,
                Component.translatable(MemeEnv.MOD_ID + ".menu.current_meme", memeName),
                x, y, 0xFF55FF55);

        super.render(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
