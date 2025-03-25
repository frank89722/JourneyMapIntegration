package me.frankv.jmi.compat.twilightforest;

import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.model.MapImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import twilightforest.init.TFMapDecorations;
import twilightforest.init.TFStructures;
import twilightforest.util.landmarks.LegacyLandmarkPlacements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;

@Slf4j
@RequiredArgsConstructor
public class BossMarker {

    public static final List<MapDecoration> markers = new ArrayList<>();

    public static int tick = 1;

    public static void ontick() {
        if (tick % 60 == 0) {
            tick = 1;
        } else {
            tick++;
            return;
        }
        var mc = Minecraft.getInstance();
        var player = mc.player;
//        var level = mc.level;
        if (player == null) return;
        var level = player.level();

//        level.registryAccess().listRegistries()
//                .forEach(registry -> log.info(registry.toString()));
//        Registry<StructureType<?>> structureRegistry =
//                level.registryAccess().registryOrThrow(Registries.STRUCTURE_TYPE);
//        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
//        var x = level.registryAccess().registryOrThrow(Registries.MAP_DECORATION_TYPE);
        if (LegacyLandmarkPlacements.chunkHasLandmarkCenter(player.chunkPosition().x, player.chunkPosition().z)) {
            ResourceKey<Structure> structureKey =
                    LegacyLandmarkPlacements.pickLandmarkForChunk(player.chunkPosition().x, player.chunkPosition().z, level);

            Map<ResourceKey<Structure>, Holder<MapDecorationType>> l = Map.ofEntries(
                    entry(TFStructures.HEDGE_MAZE, TFMapDecorations.HEDGE_MAZE),
                    entry(TFStructures.QUEST_GROVE, TFMapDecorations.QUEST_GROVE),
//                    entry(TFStructures.MUSHROOM_TOWER, GenerationStep.Decoration.SURFACE_STRUCTURES),
                    entry(TFStructures.HOLLOW_HILL_SMALL, TFMapDecorations.SMALL_HOLLOW_HILL),
                    entry(TFStructures.HOLLOW_HILL_MEDIUM, TFMapDecorations.MEDIUM_HOLLOW_HILL),
                    entry(TFStructures.HOLLOW_HILL_LARGE, TFMapDecorations.LARGE_HOLLOW_HILL),
                    entry(TFStructures.NAGA_COURTYARD, TFMapDecorations.NAGA_COURTYARD),
                    entry(TFStructures.LICH_TOWER, TFMapDecorations.LICH_TOWER),
                    entry(TFStructures.LABYRINTH, TFMapDecorations.LABYRINTH),
                    entry(TFStructures.HYDRA_LAIR, TFMapDecorations.HYDRA_LAIR),
                    entry(TFStructures.KNIGHT_STRONGHOLD, TFMapDecorations.KNIGHT_STRONGHOLD),
                    entry(TFStructures.DARK_TOWER, TFMapDecorations.DARK_TOWER),
                    entry(TFStructures.YETI_CAVE, TFMapDecorations.YETI_LAIR),
                    entry(TFStructures.AURORA_PALACE, TFMapDecorations.AURORA_PALACE),
                    entry(TFStructures.TROLL_CAVE, TFMapDecorations.TROLL_CAVES),
                    entry(TFStructures.FINAL_CASTLE, TFMapDecorations.FINAL_CASTLE));

            if (!l.containsKey(structureKey)) return;
            var icon = l.get(structureKey).value().assetId();
            var i = ResourceLocation.parse(icon.getNamespace() + ":textures/map/decorations/" + icon.getPath() +
                    ".png");
            log.info(i.toString());
            var img = new MapImage(i, 8, 8)
                    .setAnchorX(16.0d)
                    .setAnchorY(38.0d)
                    .setDisplayWidth(16.0d)
                    .setDisplayHeight(16.0d);
            var overlay = new MarkerOverlay(Constants.MOD_ID, player.blockPosition(), img)
                    .setDimension(level.dimension())
                    .setLabel("123");
            showOverlay(overlay);


//             Filters by structures we want to give icons for
//            if (structureRegistry.getHolder(structureKey).map(structureRef -> structureRef.is(StructureTagGenerator.LANDMARK)).orElse(false)) {
//                if (structureRegistry.getOrThrow(structureKey) instanceof LandmarkStructure landmark) {
//                    landmark.getMapIcon().ifPresent(icon -> {
//                        log.info(icon.getRegisteredName());
//                        showOverlay(new MarkerOverlay(Constants.MOD_ID, player.blockPosition(),
//                                new MapImage(ResourceLocation.parse(icon.getRegisteredName()), 24, 24)));
//                    });
//                    //TwilightForestMod.LOGGER.info("Found feature at {}, {}. Placing it on the map at {}, {}", worldX, worldZ, mapX, mapZ);
//                }
//            }
        }

    }

//    public void add(MapDecoration d) {
//        markers.add(d);
//        Minecraft.getInstance().player.clientLevel.registryAccess().
//                log.info("Added BossMarker: {}", d.toString());
//    }
//
//    public void remove(MapDecoration d) {
//        markers.remove(d);
//        log.info("removed BossMarker: {}", d.toString());
//    }
}
