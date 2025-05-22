package me.frankv.jmi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Utility class for drawing shapes in the game.
 * <p>
 * This class provides methods for drawing rectangles and other shapes with customizable
 * colors and transparency.
 */
public class Draw {
    /**
     * Draws a colored rectangle with the specified dimensions and transparency.
     * <p>
     * This is a convenience method that calls {@link #drawRectangle(GuiGraphics, double, double, double, double, int, float, float)}
     * with a default level of 0.0F.
     *
     * @param guiGraphics The GUI graphics context
     * @param x The x-coordinate of the top-left corner
     * @param y The y-coordinate of the top-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the rectangle as an RGB integer
     * @param alpha The transparency of the rectangle (0.0F to 1.0F)
     */
    public static void drawRectangle(GuiGraphics guiGraphics, double x, double y, double width, double height, int color, float alpha) {
        drawRectangle(guiGraphics, x, y, width, height, color, alpha, 0.0F);
    }

    /**
     * Draws a colored rectangle with the specified dimensions, transparency, and z-level.
     *
     * @param guiGraphics The GUI graphics context
     * @param x The x-coordinate of the top-left corner
     * @param y The y-coordinate of the top-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the rectangle as an RGB integer
     * @param alpha The transparency of the rectangle (0.0F to 1.0F)
     * @param level The z-level of the rectangle (depth in the rendering stack)
     */
    public static void drawRectangle(GuiGraphics guiGraphics, double x, double y, double width, double height, int color, float alpha, float level) {
        var red = (float) (color >> 16 & 255) / 255.0F;
        var green = (float) (color >> 8 & 255) / 255.0F;
        var blue = (float) (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var matrix4f = guiGraphics.pose().last().pose();
        bufferbuilder.addVertex(matrix4f, (float) x, (float) (height + y), level).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) (x + width), (float) (height + y), level).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) (x + width), (float) y, level).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) x, (float) y, level).setColor(red, green, blue, alpha);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
