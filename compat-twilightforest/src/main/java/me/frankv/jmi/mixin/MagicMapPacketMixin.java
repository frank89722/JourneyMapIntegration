package me.frankv.jmi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import twilightforest.network.MagicMapPacket;

@Mixin(value = MagicMapPacket.class, remap = false)
public class MagicMapPacketMixin {

//    @Inject(method = "handle", at = @At("RETURN"))
//    private static void injectHandle(MagicMapPacket message, IPayloadContext ctx, CallbackInfo ci) {
//        message.inner().decorations().ifPresent(d ->
//                d.forEach(ddd -> {
//                    TwilightForestCompat.BOSS_MARKER.add(ddd);
//                })
//        );
//
//    }
//
}
