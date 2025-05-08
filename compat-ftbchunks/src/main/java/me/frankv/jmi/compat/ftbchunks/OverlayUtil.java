package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.api.v2.client.model.TextProperties;

public class OverlayUtil {

    public static void disableTextForTextProps(TextProperties textProperties) {
        textProperties.setMinZoom(Integer.MAX_VALUE);
    }

    public static void enableTextForTextProps(TextProperties textProperties) {
        textProperties.setMinZoom(250);
    }

    public static int getTeamTextColor(ClientTeam team) {
        return team.getColor() << 2;
    }

}
