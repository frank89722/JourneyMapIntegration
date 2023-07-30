package me.frankv.jmi.mixin;

import me.frankv.jmi.waypointmessage.WaypointChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"))
    private void onRightClick(long l, int i, int j, int k, CallbackInfo ci) {
        if (i != 1) return;

        final var mc = Minecraft.getInstance();
        final var hitResult = mc.hitResult;

        if (mc.player == null || hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;

        WaypointChatMessage.onRightClickOnBlock(((BlockHitResult)mc.hitResult).getBlockPos(), mc.player.getMainHandItem());
    }

}
