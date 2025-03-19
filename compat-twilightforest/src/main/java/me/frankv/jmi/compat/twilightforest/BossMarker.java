package me.frankv.jmi.compat.twilightforest;

import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.model.MapImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import twilightforest.data.tags.StructureTagGenerator;
import twilightforest.util.landmarks.LegacyLandmarkPlacements;
import twilightforest.world.components.structures.util.LandmarkStructure;

import java.util.ArrayList;
import java.util.List;

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
        var level = mc.level;
        if (player == null || level == null) return;

        level.registryAccess().listRegistries()
                .forEach(registry -> log.info(registry.toString()));
        Registry<StructureType<?>> structureRegistry =
                level.registryAccess().registryOrThrow(Registries.STRUCTURE_TYPE);
        if (LegacyLandmarkPlacements.chunkHasLandmarkCenter(player.chunkPosition().x, player.chunkPosition().z)) {
            ResourceKey<Structure> structureKey =
                    LegacyLandmarkPlacements.pickLandmarkForChunk(player.chunkPosition().x, player.chunkPosition().z, level);

            structureRegistry.getHolder(structureKey)
            // Filters by structures we want to give icons for
            if (structureRegistry.getHolder(structureKey).map(structureRef -> structureRef.is(StructureTagGenerator.LANDMARK)).orElse(false)) {
                if (structureRegistry.getOrThrow(structureKey) instanceof LandmarkStructure landmark) {
                    landmark.getMapIcon().ifPresent(icon -> {
                        log.info(icon.getRegisteredName());
                        showOverlay(new MarkerOverlay(Constants.MOD_ID, player.blockPosition(),
                                new MapImage(ResourceLocation.parse(icon.getRegisteredName()), 24, 24)));
                    });
                    //TwilightForestMod.LOGGER.info("Found feature at {}, {}. Placing it on the map at {}, {}", worldX, worldZ, mapX, mapZ);
                }
            }
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
