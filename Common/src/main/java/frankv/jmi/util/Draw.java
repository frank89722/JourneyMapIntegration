package frankv.jmi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;

public class Draw
{
    public static void drawRectangle(PoseStack poseStack, double x, double y, double width, double height, int color, float alpha)
    {
        drawRectangle(poseStack, x, y, width, height, color, alpha, 0.0F);
    }

    public static void drawRectangle(PoseStack poseStack, double x, double y, double width, double height, int color, float alpha, float level)
    {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix4f = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix4f, (float) x, (float) (height + y), level).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, (float) (x + width), (float) (height + y), level).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, (float) (x + width), (float) y, level).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix4f, (float) x, (float) y, level).color(red, green, blue, alpha).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
