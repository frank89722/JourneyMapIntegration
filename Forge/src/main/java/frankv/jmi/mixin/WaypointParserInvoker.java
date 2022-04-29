package frankv.jmi.mixin;

import journeymap.client.waypoint.WaypointParser;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(WaypointParser.class)
public interface WaypointParserInvoker {
    @Invoker(value = "addWaypointMarkup", remap = false)
    static Component addWaypointMarkup(String text, List<String> matches) {
        return null;
    }
}
