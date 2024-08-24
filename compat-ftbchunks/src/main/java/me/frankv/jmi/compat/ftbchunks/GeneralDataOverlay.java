package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import journeymap.api.v2.client.fullscreen.IFullscreen;
import me.frankv.jmi.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class GeneralDataOverlay {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void onScreenDraw(Screen screen, GuiGraphics guiGraphics) {
        if (!(screen instanceof IFullscreen) || !ClaimingMode.INSTANCE.isActivated()) return;

        final var font = mc.font;
        final var list = FTBChunksClient.INSTANCE.getChunkSummary();
        var screenHeight = screen.height;
        var width = 0.0f;

        for (var comp : list) {
            final var l = font.width(comp) + 9f;
            if (l > width) {
                width = l;
            }
        }

        var backgroundH = font.lineHeight * list.size() + 6;

        Draw.drawRectangle(guiGraphics, 3, screenHeight - backgroundH - 28, width, backgroundH, 0x000000, 0.5f);

        for (var comp : list) {
            guiGraphics.drawString(font, comp, 8, screenHeight - 38, 0xffffff, true);
            screenHeight -= font.lineHeight;
        }
    }
}
