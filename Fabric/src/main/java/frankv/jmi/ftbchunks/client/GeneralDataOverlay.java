package frankv.jmi.ftbchunks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import journeymap.client.api.event.fabric.FabricEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.ui.fullscreen.Fullscreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.LinkedList;

public class GeneralDataOverlay {

    public GeneralDataOverlay() {
        ScreenEvents.AFTER_INIT.register(this::onScreen);

    }

    public void onScreen(Minecraft mc, Screen screen, int i, int i1) {
        if (!(screen instanceof Fullscreen)) return;

        ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) -> {
            if (!ClaimingMode.activated) return;

            var d = FTBChunksClient.generalData;
            if (d == null) return;

            var font = mc.font;
            var list = new LinkedList<Component>();
            var screenHeight = (float)screen.height;
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

            DrawUtil.drawRectangle(stack, 3, screenHeight - backgroundH - 4, width, backgroundH, 0x000000, 0.5f);

            for(var comp : list) {
                font.draw(stack, comp, 8f, screenHeight - 15, 1);
                screenHeight -= font.lineHeight;
            }
        });

    }
}
