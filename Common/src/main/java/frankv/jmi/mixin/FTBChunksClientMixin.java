package frankv.jmi.mixin;

import dev.ftb.mods.ftbchunks.client.ChunkScreen;
import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import frankv.jmi.JMI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FTBChunksClient.class)
public class FTBChunksClientMixin {

    @Inject(method = "openGui", at = @At("HEAD"), cancellable = true, remap = false)
    private static void regionScreenFirst(CallbackInfo ci) {
        if (!JMI.clientConfig.getShowClaimChunkScreen()) return;
        (new ChunkScreen()).openGui();
        ci.cancel();
    }
}
