package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.api.v2.client.model.TextProperties;

public class OverlayUtil {

    public static void disableTextForTextProps(TextProperties textProperties) {
        textProperties.setOpacity(0f);
        textProperties.setBackgroundOpacity(0f);
    }

    public static void enableTextForTextProps(TextProperties textProperties) {
        textProperties.setOpacity(1f);
        textProperties.setBackgroundOpacity(1f);
    }

    public static int getTeamTextColor(ClientTeam team) {
        return team.getColor() << 2;
    }

}
