package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import journeymap.api.v2.client.fullscreen.IFullscreen;
import me.frankv.jmi.compat.ftbchunks.claimingmode.ClaimingMode;
import me.frankv.jmi.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GeneralDataOverlay {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void onScreenDraw(Screen screen, GuiGraphicsExtractor guiGraphicsExtractor) {
        if (!(screen instanceof IFullscreen) || !ClaimingMode.INSTANCE.isActivated()) return;

        final var font = mc.font;
        final var data = FTBChunksClient.INSTANCE.getGeneralChunkData();
        final List<Component> list = List.of(
                Component.translatable("ftbchunks.gui.claimed").append(": " + data.claimed() + " / " + data.maxClaimChunks()),
                Component.translatable("ftbchunks.gui.force_loaded").append(": " + data.loaded() + " / " + data.maxForceLoadChunks())
        );
        final var padding = 4;
        final var left = 3;
        final var bottom = screen.height - 28;

        // font.width measures the actual rendered glyphs, so it works for any language (CJK, etc.)
        var textWidth = 0;
        for (var comp : list) {
            textWidth = Math.max(textWidth, font.width(comp));
        }

        final var backgroundW = textWidth + padding * 2;
        final var backgroundH = font.lineHeight * list.size() + padding * 2;

        Draw.drawRectangle(guiGraphicsExtractor, left, bottom - backgroundH, backgroundW, backgroundH, 0x000000, 0.5f);

        // First entry at the bottom, following entries stacked upwards
        for (var i = 0; i < list.size(); i++) {
            final var y = bottom - padding - font.lineHeight * (i + 1);
            guiGraphicsExtractor.text(font, list.get(i), left + padding, y, 0xFFFFFFFF, true);
        }
    }
}
