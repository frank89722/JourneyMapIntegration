package frankv.jmi.mixin;

import journeymap.client.waypoint.WaypointParser;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(WaypointParser.class)
public interface WaypointParserInvoker {
    @Invoker("addWaypointMarkup")
    static ITextComponent addWaypointMarkup(String text, List<String> matches) {
        return null;
    }
}
