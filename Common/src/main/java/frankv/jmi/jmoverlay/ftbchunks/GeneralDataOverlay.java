package frankv.jmi.jmoverlay.ftbchunks;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import frankv.jmi.JMI;
import frankv.jmi.util.Draw;
import journeymap.client.api.model.IFullscreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;

public class GeneralDataOverlay {
    private static Minecraft mc = Minecraft.getInstance();

    public static void onScreenDraw(Screen screen, PoseStack stack) {
        if (!(screen instanceof IFullscreen) || !JMI.ftbchunks || !ClaimingMode.INSTANCE.isActivated()) return;

        final var d = FTBChunksClient.generalData;
        if (d == null) return;

        final var font = mc.font;
        final var list = new LinkedList<Component>();
        var screenHeight = (float)screen.height;
        var width = 0.0f;

        list.add((Component.literal(d.loaded + " / " + d.maxForceLoadChunks)).withStyle(d.loaded > d.maxForceLoadChunks ? ChatFormatting.RED : (d.loaded == d.maxForceLoadChunks ? ChatFormatting.YELLOW : ChatFormatting.GREEN)));
        list.add(Component.translatable("ftbchunks.gui.force_loaded").withStyle(ChatFormatting.WHITE));
        list.add((Component.literal(d.claimed + " / " + d.maxClaimChunks)).withStyle(d.claimed > d.maxClaimChunks ? ChatFormatting.RED : (d.claimed == d.maxClaimChunks ? ChatFormatting.YELLOW : ChatFormatting.GREEN)));
        list.add(Component.translatable("ftbchunks.gui.claimed").withStyle(ChatFormatting.WHITE));

        for(var comp : list) {
            final var l = font.width(comp) + 9f;
            if (l > width) width = l;
        }

        var backgroundH = font.lineHeight * list.size() + 6;

        Draw.drawRectangle(stack, 3, screenHeight - backgroundH - 4, width, backgroundH, 0x000000, 0.5f);

        for(var comp : list) {
            font.draw(stack, comp, 8f, screenHeight - 15, 1);
            screenHeight -= font.lineHeight;
        }
    }
}
