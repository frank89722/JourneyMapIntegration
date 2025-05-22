package me.frankv.jmi.api.jmoverlay;

import journeymap.api.v2.client.fullscreen.IThemeButton;
import net.minecraft.resources.ResourceLocation;

/**
 * Interface for toggleable overlays in JourneyMap.
 * <p>
 * Implementations of this interface represent overlays that can be toggled on and off
 * via buttons in JourneyMap's UI. These overlays are provided by compatibility modules
 * and are used to display mod-specific information on the map.
 * <p>
 * Compatibility modules should return their toggleable overlays from the
 * {@link me.frankv.jmi.api.ModCompat#getToggleableOverlays()} method.
 */
public interface ToggleableOverlay {

    /**
     * Called when the overlay is toggled on or off.
     * <p>
     * This method should update the overlay's state and show or hide it as appropriate.
     *
     * @param button The button that was clicked to toggle the overlay
     */
    void onToggle(IThemeButton button);

    /**
     * Gets the label to display on the button for this overlay.
     *
     * @return The button label
     */
    String getButtonLabel();

    /**
     * Gets the icon to display on the button for this overlay.
     *
     * @return The button icon resource location
     */
    ResourceLocation getButtonIconName();

    /**
     * Gets the order in which this overlay's button should appear.
     * <p>
     * Lower values will appear first.
     *
     * @return The button order
     */
    int getOrder();

    /**
     * Checks if this overlay is currently activated.
     *
     * @return true if the overlay is activated, false otherwise
     */
    boolean isActivated();
}
