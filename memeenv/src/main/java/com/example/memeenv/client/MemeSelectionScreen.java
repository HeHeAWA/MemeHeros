package com.example.memeenv.client;

import com.example.memeheroes.api.MemeBridge;
import com.example.memeenv.MemeEnv;
import com.example.memeenv.network.C2SMemeSelectPacket;
import com.example.memeenv.network.ModMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MemeSelectionScreen extends Screen {
    private static final int PER_PAGE = 5;
    private static final int GAP = 24;
    private static final int START_Y = 45;

    private final boolean fromItem;
    private int currentPage = 0;

    public MemeSelectionScreen(boolean fromItem) {
        super(Component.translatable(MemeEnv.MOD_ID + ".screen.title"));
        this.fromItem = fromItem;
    }

    private int totalPages() {
        int total = MemeBridge.getAll().size();
        return Math.max(1, (total + PER_PAGE - 1) / PER_PAGE);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonW = 240;
        int buttonH = 20;
        int paginationY = START_Y + PER_PAGE * GAP + 4;

        int pages = totalPages();
        if (currentPage < 0) currentPage = 0;
        if (currentPage >= pages) currentPage = pages - 1;

        var memes = MemeBridge.getAll();
        int startIndex = currentPage * PER_PAGE;
        int endIndex = Math.min(startIndex + PER_PAGE, memes.size());

        for (int i = startIndex; i < endIndex; i++) {
            MemeBridge.MemeEntry m = memes.get(i);
            int row = i - startIndex;
            addRenderableWidget(Button.builder(
                            Component.translatable(m.translationKey()),
                            b -> onSelect(m))
                    .bounds(centerX - buttonW / 2, START_Y + row * GAP, buttonW, buttonH)
                    .build());
        }

        if (pages > 1) {
            addRenderableWidget(Button.builder(
                            Component.translatable(MemeEnv.MOD_ID + ".screen.prev"),
                            b -> { currentPage--; rebuildWidgets(); })
                    .bounds(centerX - 130, paginationY, 60, 20)
                    .build()).active = (currentPage > 0);

            addRenderableWidget(Button.builder(
                            Component.translatable(MemeEnv.MOD_ID + ".screen.next"),
                            b -> { currentPage++; rebuildWidgets(); })
                    .bounds(centerX + 70, paginationY, 60, 20)
                    .build()).active = (currentPage < pages - 1);
        }

        if (fromItem) {
            addRenderableWidget(Button.builder(
                            Component.translatable("gui.cancel"),
                            b -> onClose())
                    .bounds(centerX - 50, paginationY + 28, 100, 20)
                    .build());
        }
    }

    private void onSelect(MemeBridge.MemeEntry m) {
        ModMessages.INSTANCE.sendToServer(new C2SMemeSelectPacket(m.getId(), fromItem));
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        int pages = totalPages();
        if (pages > 1) {
            int paginationY = START_Y + PER_PAGE * GAP + 4;
            Component pageText = Component.translatable(MemeEnv.MOD_ID + ".screen.page", currentPage + 1, pages);
            guiGraphics.drawCenteredString(this.font, pageText, this.width / 2, paginationY + 6, 0xFFFFAA00);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
