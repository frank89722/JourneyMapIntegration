package me.frankv.jmi.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public class Draw {
    public static void drawRectangle(GuiGraphicsExtractor guiGraphics, double x, double y, double width, double height,
                                     int color, float alpha) {
        drawRectangle(guiGraphics, x, y, width, height, color, alpha, 0.0F);
    }

    /**
     * @param level unused since 26.1; GUI elements are layered by submission order.
     */
    public static void drawRectangle(GuiGraphicsExtractor guiGraphics, double x, double y, double width, double height,
                                     int color, float alpha, float level) {
        var a = Mth.clamp(Math.round(alpha * 255.0F), 0, 255);
        var argb = (a << 24) | (color & 0xFFFFFF);
        guiGraphics.fill(
                (int) Math.round(x), (int) Math.round(y),
                (int) Math.round(x + width), (int) Math.round(y + height),
                argb
        );
    }
}
