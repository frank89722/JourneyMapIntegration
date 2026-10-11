package me.frankv.jmi.mixin;

import me.frankv.jmi.JMI;
import me.frankv.jmi.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    private void onRightClick(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        if (action != 1) return;

        final var mc = Minecraft.getInstance();
        final var hitResult = mc.hitResult;

        if (mc.player == null || hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;

        JMI.getJmiEventBus().sendEvent(
                new Event.PlayerInteract(((BlockHitResult)mc.hitResult).getBlockPos(), mc.player.getMainHandItem()));
    }

}
