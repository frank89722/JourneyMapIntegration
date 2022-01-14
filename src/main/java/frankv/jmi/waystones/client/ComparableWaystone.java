package frankv.jmi.waystones.client;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ComparableWaystone {
    public final UUID uuid;
    public final String name;
    public final BlockPos pos;
    public final RegistryKey<World> dim;

    private ComparableWaystone(UUID uuid, String name, BlockPos pos, RegistryKey<World> dim) {
        this.uuid = uuid;
        this.name = name;
        this.pos = pos;
        this.dim = dim;
    }

    public static Set<ComparableWaystone> fromEvent(KnownWaystonesEvent event) {
        Set<ComparableWaystone> waystones = new HashSet<>();

        for (IWaystone w : event.getWaystones()) {
            if (!w.hasName()) continue;
            waystones.add(new ComparableWaystone(w.getWaystoneUid(), w.getName(), w.getPos(), w.getDimension()));
        }

        return waystones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparableWaystone that = (ComparableWaystone) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name) && pos.compareTo(that.pos) == 0 && dim.compareTo(that.dim) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, pos, dim);
    }
}
