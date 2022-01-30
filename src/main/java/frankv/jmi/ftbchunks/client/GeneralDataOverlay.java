package frankv.jmi.ftbchunks.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.net.SendGeneralDataPacket;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.ui.fullscreen.Fullscreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;

public class GeneralDataOverlay {
    private static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onScreenDraw(GuiScreenEvent.DrawScreenEvent event) {
        if (!(event.getGui() instanceof Fullscreen) || !ClaimingMode.activated) return;

        SendGeneralDataPacket d = FTBChunksClient.generalData;
        if (d == null) return;

        FontRenderer font = mc.font;
        MatrixStack stack = event.getMatrixStack();
        LinkedList<ITextComponent> list = new LinkedList<>();
        float screenHeight = (float)mc.screen.height;
        float width = 0.0f;

        list.add((new StringTextComponent(d.loaded + " / " + d.maxForceLoadChunks)).withStyle(d.loaded > d.maxForceLoadChunks ? TextFormatting.RED : (d.loaded == d.maxForceLoadChunks ? TextFormatting.YELLOW : TextFormatting.GREEN)));
        list.add(new TranslationTextComponent("ftbchunks.gui.force_loaded").withStyle(TextFormatting.WHITE));
        list.add((new StringTextComponent(d.claimed + " / " + d.maxClaimChunks)).withStyle(d.claimed > d.maxClaimChunks ? TextFormatting.RED : (d.claimed == d.maxClaimChunks ? TextFormatting.YELLOW : TextFormatting.GREEN)));
        list.add(new TranslationTextComponent("ftbchunks.gui.claimed").withStyle(TextFormatting.WHITE));

        for(ITextComponent comp : list) {
            float l = font.width(comp) + 9f;
            if (l > width) width = l;
        }

        float backgroundH = font.lineHeight * list.size() + 6;

        DrawUtil.drawRectangle(stack, 3, screenHeight - backgroundH - 4, width, backgroundH, 0x000000, 0.5f);

        for(ITextComponent comp : list) {
            font.draw(stack, comp, 8f, screenHeight - 15, 1);
            screenHeight -= font.lineHeight;
        }
    }
}
