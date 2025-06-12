package me.frankv.jmi.compat.openpac.claimedchunksoverlay;

import journeymap.api.v2.client.fullscreen.IThemeButton;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@Slf4j
public enum ClaimedChunksOverlay implements ToggleableOverlay {
    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void onToggle(IThemeButton button) {
    }

    @Override
    public String getButtonLabel() {
        return "";
    }

    public void onJMMapping(Event.JMMappingEvent e) {
        switch (e.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
            }

            case MAPPING_STOPPED -> {
            }
        }

    }

    @Override
    public ResourceLocation getButtonIconName() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "images/ftb.png");
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean isActivated() {
        return false;
    }

}