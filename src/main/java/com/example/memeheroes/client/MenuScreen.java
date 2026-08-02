package com.example.memeheroes.client;

import com.example.memeheroes.meme.MemeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MenuScreen extends Screen {

    public MenuScreen() {
        super(Component.translatable("memeheroes.menu.title"));
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

        // 击杀数（来自服务端）
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.kills", ClientMenuData.getKillCount()),
                x, y, 0xFFFFFF);
        y += lineH;

        // 血量（客户端本地）
        float health = player == null ? 0.0F : player.getHealth();
        float maxHealth = player == null ? 0.0F : player.getMaxHealth();
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.health",
                        String.format("%.1f", health), String.format("%.1f", maxHealth)),
                x, y, 0xFFFFFF);
        y += lineH;

        // 饱食度
        int food = player == null ? 0 : player.getFoodData().getFoodLevel();
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.food", food),
                x, y, 0xFFFFFF);
        y += lineH;

        // 护甲值
        int armor = player == null ? 0 : player.getArmorValue();
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.armor", armor),
                x, y, 0xFFFFFF);
        y += lineH;

        // 经验等级
        int level = player == null ? 0 : player.experienceLevel;
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.level", level),
                x, y, 0xFFFFAA00);
        y += lineH;

        // 当前梗
        int memeId = ClientMenuData.getSelectedMemeId();
        Component memeName = (memeId == 0 || MemeType.byId(memeId) == null)
                ? Component.translatable("memeheroes.menu.no_meme")
                : Component.translatable(MemeType.byId(memeId).translationKey());
        gg.drawString(this.font,
                Component.translatable("memeheroes.menu.current_meme", memeName),
                x, y, 0xFF55FF55);

        super.render(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
