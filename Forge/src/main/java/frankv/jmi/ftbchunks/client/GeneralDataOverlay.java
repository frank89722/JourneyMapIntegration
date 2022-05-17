package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import frankv.jmi.util.Draw;
import journeymap.client.api.model.IFullscreen;
import journeymap.client.render.draw.DrawUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;

public class GeneralDataOverlay {
    private static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onScreenDraw(ScreenEvent.DrawScreenEvent event) {
        if (!(event.getScreen() instanceof IFullscreen screen) || !ClaimingMode.activated) return;

        var d = FTBChunksClient.generalData;
        if (d == null) return;

        var font = mc.font;
        var stack = event.getPoseStack();
        var list = new LinkedList<Component>();
        var screenHeight = (float)screen.getScreen().height;
        var width = 0.0f;

        list.add((new TextComponent(d.loaded + " / " + d.maxForceLoadChunks)).withStyle(d.loaded > d.maxForceLoadChunks ? ChatFormatting.RED : (d.loaded == d.maxForceLoadChunks ? ChatFormatting.YELLOW : ChatFormatting.GREEN)));
        list.add(new TranslatableComponent("ftbchunks.gui.force_loaded").withStyle(ChatFormatting.WHITE));
        list.add((new TextComponent(d.claimed + " / " + d.maxClaimChunks)).withStyle(d.claimed > d.maxClaimChunks ? ChatFormatting.RED : (d.claimed == d.maxClaimChunks ? ChatFormatting.YELLOW : ChatFormatting.GREEN)));
        list.add(new TranslatableComponent("ftbchunks.gui.claimed").withStyle(ChatFormatting.WHITE));

        for(var comp : list) {
            var l = font.width(comp) + 9f;
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
