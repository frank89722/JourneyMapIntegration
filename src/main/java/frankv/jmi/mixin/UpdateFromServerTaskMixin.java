package frankv.jmi.mixin;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.UpdateChunkFromServerTask;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(UpdateChunkFromServerTask.class)
public class UpdateFromServerTaskMixin {

    @Shadow @Final private MapDimension dimension;

    @Shadow @Final private SendChunkPacket.SingleChunk chunk;

    @Shadow @Final private UUID teamId;

    @Inject(method = "runMapTask", at = @At("RETURN"), remap = false)
    private void onRunMapTask(CallbackInfo ci) {
        ClaimedChunkPolygon.addToQueue(dimension, chunk, teamId);
    }
}
