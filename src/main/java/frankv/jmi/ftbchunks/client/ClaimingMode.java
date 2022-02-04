package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftblibrary.math.XZ;
import frankv.jmi.JMI;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.display.ThemeButtonDisplay;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import journeymap.client.api.model.*;
import journeymap.client.api.util.PolygonHelper;
import journeymap.client.ui.component.JmUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ClaimingMode {
    private IClientAPI jmAPI;
    private ClaimedChunkPolygon claimedChunkPolygon;
    public static boolean activated = false;
    private static Minecraft mc = Minecraft.getInstance();
    public static PolygonOverlay claimAreaPolygon = null;
    public static Set<ChunkPos> area = new HashSet<>();

    public ClaimingMode(IClientAPI jmAPI, ClaimedChunkPolygon claimedChunkPolygon) {
        this.jmAPI = jmAPI;
        this.claimedChunkPolygon = claimedChunkPolygon;
        this.area = new HashSet<>();
    }

    @SubscribeEvent
    public void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        ThemeButtonDisplay buttonDisplay = event.getThemeButtonDisplay();
        buttonDisplay.addThemeToggleButton("FTBChunks Claiming Mode", "FTBChunks Claiming Mode", "grid", activated, b -> buttonControl(b));
    }

    @SubscribeEvent
    public void onGuiScreen(GuiOpenEvent event) {
        if (!activated) return;
        if (event.getGui() instanceof JmUI && event.getGui() != null) return;
        activated = false;
        removeOverlays();
    }


    private void buttonControl(IThemeButton button) {
        if (mc.player == null) return;
        if (!activated) {
            activated = true;
            createClaimingAreaOverlays();
            button.setToggled(true);
        } else {
            activated = false;
            removeOverlays();
            button.setToggled(false);
        }
    }

    private void removeOverlays() {
        if (claimAreaPolygon == null) return;
        jmAPI.remove(claimAreaPolygon);

        claimedChunkPolygon.showForceLoadedByArea(false);
        claimAreaPolygon = null;
        area.clear();
    }

    public static PolygonOverlay dragPolygon(XZ xz) {
        final ClientPlayerEntity player = Minecraft.getInstance().player;

        String displayId = "drag_polygon_" + xz.x + "_" + xz.z;

        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(0xffffff).setFillOpacity(.3f);

        MapPolygon polygon = PolygonHelper.createChunkPolygon(xz.x, 10, xz.z);

        return new PolygonOverlay(JMI.MODID, displayId, player.clientLevel.dimension(), shapeProps, polygon);
    }

    public static PolygonOverlay forceLoadedPolygon(ChunkDimPos pos) {
        final ClientPlayerEntity player = Minecraft.getInstance().player;

        String displayId = "ftb_force_loaded_" + pos.x + "_" + pos.z;

        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(2).setStrokeColor(0xff0000)
                .setFillOpacity(0f);

        MapPolygon polygon = PolygonHelper.createChunkPolygon(pos.x, 10, pos.z);

        return new PolygonOverlay(JMI.MODID, displayId, player.clientLevel.dimension(), shapeProps, polygon);
    }

    private void createClaimingAreaOverlays() {
        final ClientPlayerEntity player = Minecraft.getInstance().player;
        final ChunkPos startPoint = new ChunkPos(player.xChunk-7, player.zChunk-7);

        String displayId = "claim_mode";

        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(3)
                .setStrokeColor(0xffffff).setStrokeOpacity(1.0f)
                .setFillOpacity(0f);

        for (int x=0; x<15; ++x) {
            for (int z=0; z<15; ++z){
                this.area.add(new ChunkPos(startPoint.x+x, startPoint.z+z));
            }
        }

        claimedChunkPolygon.showForceLoadedByArea(true);

        final List<MapPolygonWithHoles> polygons = PolygonHelper.createChunksPolygon(this.area, 100);

        PolygonOverlay overlay = new PolygonOverlay(JMI.MODID, displayId, player.level.dimension(), shapeProps, polygons.get(0));
        if (JMIOverlayHelper.createPolygon(overlay)) claimAreaPolygon = overlay;
    }
}
