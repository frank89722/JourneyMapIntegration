/*package frankv.jmi.ftbchunks;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManager;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.event.ClaimedChunkEvent;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.event.TeamEvent;
import frankv.jmi.JMI;
import frankv.jmi.NetworkHandler;
import frankv.jmi.ftbchunks.network.PacketClaimedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;

public class FTBChunksEventHandler {
    private static ClaimedChunkManager chunkManager = null;

    public FTBChunksEventHandler() {
        ClaimedChunkEvent.AFTER_CLAIM.register((source, chunk) -> NetworkHandler.sendFTBToClient(new PacketClaimedData(chunk, true, false), chunk.getPos().dimension));
        ClaimedChunkEvent.AFTER_UNCLAIM.register((source, chunk) -> NetworkHandler.sendFTBToClient(new PacketClaimedData(chunk, false, false), chunk.getPos().dimension));
        TeamEvent.PROPERTIES_CHANGED.register((team) -> resend(team.getTeam()));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!JMI.COMMON_CONFIG.getFTBChunks()) return;
        if (chunkManager == null) chunkManager = FTBChunksAPI.getManager();
        sendAllData(event.getPlayer(), chunkManager.getAllClaimedChunks());
    }

    @SubscribeEvent
    public void onPlayerChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!JMI.COMMON_CONFIG.getFTBChunks()) return;
        sendAllData(event.getPlayer(), chunkManager.getAllClaimedChunks());
    }

    private void resend(Team team) {
        if (!JMI.COMMON_CONFIG.getFTBChunks()) return;
        Collection<ClaimedChunk> chunks = chunkManager.getData(team).getClaimedChunks();
        for (ClaimedChunk c : chunks) {
            NetworkHandler.sendFTBToClient(new PacketClaimedData(c, false, true));
        }
    }

    private void sendAllData(PlayerEntity player, Collection<ClaimedChunk> chunks) {
        if (!JMI.COMMON_CONFIG.getFTBChunks()) return;
        final RegistryKey<World> dim = player.level.dimension();

        for (ClaimedChunk c : chunks) {
            if (c.getPos().dimension == dim) {
                NetworkHandler.sendFTBToClient(new PacketClaimedData(c, true, false), (ServerPlayerEntity) player);
            }
        }
    }
}

 */
