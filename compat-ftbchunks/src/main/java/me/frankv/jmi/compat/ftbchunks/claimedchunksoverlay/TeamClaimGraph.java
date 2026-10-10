package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import net.minecraft.world.level.ChunkPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tracks one team's claimed chunks in one dimension as 4-connected components, so a claim change only
 * rebuilds the components it touches.
 */
public final class TeamClaimGraph {

    public record RebuildPlan(Set<Long> staleComponents, List<Set<ChunkPos>> newComponents) {
    }

    private final Set<ChunkPos> chunks = new HashSet<>();
    private final Map<ChunkPos, Long> componentOf = new HashMap<>();
    private final Map<Long, Set<ChunkPos>> components = new HashMap<>();
    private final Set<ChunkPos> dirty = new HashSet<>();
    private long nextComponentId = 1;
    private boolean building;

    public boolean add(ChunkPos pos) {
        if (!chunks.add(pos)) return false;
        dirty.add(pos);
        return true;
    }

    public boolean remove(ChunkPos pos) {
        if (!chunks.remove(pos)) return false;
        dirty.add(pos);
        return true;
    }

    public boolean contains(ChunkPos pos) {
        return chunks.contains(pos);
    }

    public boolean isEmpty() {
        return chunks.isEmpty();
    }

    public boolean hasDirty() {
        return !dirty.isEmpty();
    }

    public boolean isBuilding() {
        return building;
    }

    public Map<Long, Set<ChunkPos>> components() {
        return Collections.unmodifiableMap(components);
    }

    /**
     * Consumes the dirty set and returns what must be rebuilt, or null when nothing changed
     * or a rebuild is already in flight. Marks the graph as building until {@link #commit} runs.
     */
    public RebuildPlan plan() {
        if (building || dirty.isEmpty()) return null;

        final var stale = new HashSet<Long>();
        final var candidates = new HashSet<ChunkPos>();
        for (var pos : dirty) {
            final var own = componentOf.get(pos);
            if (own != null) stale.add(own);
            if (chunks.contains(pos)) candidates.add(pos);
            for (var neighbour : neighbours(pos)) {
                final var id = componentOf.get(neighbour);
                if (id != null) stale.add(id);
            }
        }
        dirty.clear();

        for (var id : stale) {
            for (var pos : components.get(id)) {
                if (chunks.contains(pos)) candidates.add(pos);
            }
        }

        final var newComponents = split(candidates);
        if (stale.isEmpty() && newComponents.isEmpty()) return null;

        building = true;
        return new RebuildPlan(Set.copyOf(stale), newComponents);
    }

    /**
     * Applies a plan: drops the stale components and registers the new ones.
     *
     * @return the ids assigned to {@code plan.newComponents()}, in order
     */
    public List<Long> commit(RebuildPlan plan) {
        for (var id : plan.staleComponents()) {
            final var old = components.remove(id);
            if (old == null) continue;
            for (var pos : old) {
                componentOf.remove(pos, id);
            }
        }

        final var ids = new ArrayList<Long>(plan.newComponents().size());
        for (var component : plan.newComponents()) {
            final var id = nextComponentId++;
            components.put(id, component);
            for (var pos : component) {
                componentOf.put(pos, id);
            }
            ids.add(id);
        }
        building = false;
        return ids;
    }

    private static List<Set<ChunkPos>> split(Set<ChunkPos> candidates) {
        final var result = new ArrayList<Set<ChunkPos>>();
        final var unvisited = new HashSet<>(candidates);
        final var queue = new ArrayDeque<ChunkPos>();

        while (!unvisited.isEmpty()) {
            final var start = unvisited.iterator().next();
            unvisited.remove(start);
            final var component = new HashSet<ChunkPos>();
            queue.add(start);
            while (!queue.isEmpty()) {
                final var pos = queue.poll();
                component.add(pos);
                for (var neighbour : neighbours(pos)) {
                    if (unvisited.remove(neighbour)) queue.add(neighbour);
                }
            }
            result.add(component);
        }
        return result;
    }

    private static ChunkPos[] neighbours(ChunkPos pos) {
        return new ChunkPos[]{
                new ChunkPos(pos.x + 1, pos.z),
                new ChunkPos(pos.x - 1, pos.z),
                new ChunkPos(pos.x, pos.z + 1),
                new ChunkPos(pos.x, pos.z - 1)
        };
    }
}
