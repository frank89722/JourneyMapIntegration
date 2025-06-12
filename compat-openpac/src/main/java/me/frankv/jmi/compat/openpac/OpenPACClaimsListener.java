package me.frankv.jmi.compat.openpac;

import net.minecraft.resources.ResourceLocation;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.tracker.api.IClaimsManagerListenerAPI;

public enum OpenPACClaimsListener implements IClaimsManagerListenerAPI {
    INSTANCE;

    @Override
    public void onWholeRegionChange(ResourceLocation dimension, int regionX, int regionZ) {

    }

    @Override
    public void onChunkChange(ResourceLocation dimension, int chunkX, int chunkZ, IPlayerChunkClaimAPI claim) {

    }

    @Override
    public void onDimensionChange(ResourceLocation dimension) {

    }
}
