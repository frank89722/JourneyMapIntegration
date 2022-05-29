package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftblibrary.math.XZ;
import frankv.jmi.JMI;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import journeymap.client.api.model.IFullscreen;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class ClaimingMode {
    private static IClientAPI jmAPI;
    public static boolean activated = false;
    private static Minecraft mc = Minecraft.getInstance();
    public static PolygonOverlay claimAreaPolygon = null;
    public static Set<ChunkPos> area = new HashSet<>();

    public static void init(IClientAPI jmAPI) {
        ClaimingMode.jmAPI = jmAPI;
    }

//    @SubscribeEvent
//    public static void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
//        var buttonDisplay = event.getThemeButtonDisplay();
//        buttonDisplay.addThemeToggleButton("FTBChunks Claiming Mode", "FTBChunks Claiming Mode", "grid", activated, b -> buttonControl(b));
//    }

//    @SubscribeEvent
    public static void onGuiScreen(Screen screen) {
        if (!activated) return;
        if (screen instanceof IFullscreen && screen != null) return;
        activated = false;
        removeOverlays();
    }

    public static void buttonControl(IThemeButton button) {
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

    private static void removeOverlays() {
        if (claimAreaPolygon == null) return;
        jmAPI.remove(claimAreaPolygon);

        ClaimedChunkPolygon.showForceLoadedByArea(false);
        claimAreaPolygon = null;
        area.clear();
    }

    public static PolygonOverlay dragPolygon(XZ xz) {
        var player = Minecraft.getInstance().player;
        var displayId = "drag_polygon_" + xz.x + "_" + xz.z;

        var shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(0xffffff).setFillOpacity(.3f);

        var polygon = PolygonHelper.createChunkPolygon(xz.x, 10, xz.z);

        return new PolygonOverlay(JMI.MOD_ID, displayId, player.clientLevel.dimension(), shapeProps, polygon);
    }

    public static PolygonOverlay forceLoadedPolygon(ChunkDimPos pos) {
        var player = Minecraft.getInstance().player;
        var displayId = "ftb_force_loaded_" + pos.x + "_" + pos.z;

        var shapeProps = new ShapeProperties()
                .setStrokeWidth(2).setStrokeColor(0xff0000)
                .setFillOpacity(0f);

        var polygon = PolygonHelper.createChunkPolygon(pos.x, 10, pos.z);

        return new PolygonOverlay(JMI.MOD_ID, displayId, player.clientLevel.dimension(), shapeProps, polygon);
    }

    private static void createClaimingAreaOverlays() {
        var player = Minecraft.getInstance().player;
        var startPoint = new ChunkPos(player.chunkPosition().x-7, player.chunkPosition().z-7);

        var displayId = "claim_mode";
        var shapeProps = new ShapeProperties()
                .setStrokeWidth(3)
                .setStrokeColor(0xffffff).setStrokeOpacity(1.0f)
                .setFillOpacity(0f);

        for (var x=0; x<15; x++) {
            for (var z=0; z<15; z++){
                area.add(new ChunkPos(startPoint.x+x, startPoint.z+z));
            }
        }

        ClaimedChunkPolygon.showForceLoadedByArea(true);

        var polygons = PolygonHelper.createChunksPolygon(area, 100);

        var overlay = new PolygonOverlay(JMI.MOD_ID, displayId, player.level.dimension(), shapeProps, polygons.get(0));
        if (JMIOverlayHelper.createPolygon(overlay)) claimAreaPolygon = overlay;
    }
}
