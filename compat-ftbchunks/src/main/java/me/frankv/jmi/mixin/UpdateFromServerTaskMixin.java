package me.frankv.jmi.mixin;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.UpdateChunkFromServerTask;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay.ClaimedChunksOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(UpdateChunkFromServerTask.class)
public class UpdateFromServerTaskMixin {

    @Shadow(remap = false)
    @Final
    private MapDimension dimension;

    @Shadow(remap = false)
    @Final
    private UUID teamId;

    @Shadow(remap = false)
    @Final
    private ChunkSyncInfo info;

    @Inject(method = "runMapTask", at = @At("RETURN"), remap = false)
    private void onRunMapTask(CallbackInfo ci) {
        ClaimedChunksOverlay.INSTANCE.addToQueue(dimension, info, teamId);
    }
}
