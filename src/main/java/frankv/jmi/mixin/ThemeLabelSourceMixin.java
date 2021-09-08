package frankv.jmi.mixin;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import journeymap.client.ui.theme.ThemeLabelSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

@Mixin(ThemeLabelSource.class)
@Unique
public class ThemeLabelSourceMixin {

    @Shadow
    @Final
    @Mutable
    private static ThemeLabelSource[] $VALUES;

    private static final ThemeLabelSource ClaimedInfo = themeLabelSourceExtra$addVariant("ClaimedInfo", "jmi.theme.lablesource.claimed", 1000L, 1L, ThemeLabelSourceMixin::getClaimedChunk);

    @Invoker("<init>")
    public static ThemeLabelSource themeLabelSourceExtra$invokeInit(String internalName, int internalId, String key, long cacheMillis, long granularityMillis, Supplier<String> supplier) {
        throw new AssertionError();
    }

    private static ThemeLabelSource themeLabelSourceExtra$addVariant(String internalName, String key, long cacheMillis, long granularityMillis, Supplier<String> supplier) {
        ArrayList<ThemeLabelSource> variants = new ArrayList<>(Arrays.asList(ThemeLabelSourceMixin.$VALUES));
        ThemeLabelSource labelSource = themeLabelSourceExtra$invokeInit(internalName,variants.get(variants.size() - 1).ordinal() + 1, key, cacheMillis, granularityMillis, supplier);
        variants.add(labelSource);
        ThemeLabelSourceMixin.$VALUES = variants.toArray(new ThemeLabelSource[0]);
        return labelSource;
    }

    private static String getClaimedChunk() {
        return ClaimedChunkPolygon.getPolygonTitleByPlayerPos();
    }
}
