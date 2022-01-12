package frankv.jmi.ftbchunks.network;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClaimedData {
    private ResourceLocation dim;
    private int x, z;
    private String teamName;
    private int teamColor;
    private boolean isAdd;
    private boolean replace;
    private boolean forceLoaded;

    public PacketClaimedData(ClaimedChunk chunk, boolean isAdd, boolean replace) {
        this.x = chunk.getPos().getChunkPos().x;
        this.z = chunk.getPos().getChunkPos().z;
        this.teamName = chunk.getTeamData().getTeam().getDisplayName();
        this.teamColor = chunk.getTeamData().getTeam().getColor();
        this.isAdd = isAdd;
        this.dim = chunk.getPos().dimension.location();
        this.replace = replace;
        this.forceLoaded = chunk.isForceLoaded();
    }

    public PacketClaimedData(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace, boolean forceLoaded) {
        this.x = x;
        this.z = z;
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.isAdd = isAdd;
        this.dim = dim;
        this.replace = replace;
        this.forceLoaded = forceLoaded;
    }

    public static PacketClaimedData read(FriendlyByteBuf buf){
        return new PacketClaimedData(buf.readResourceLocation(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public static void write(PacketClaimedData msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.dim);
        buf.writeVarInt(msg.x);
        buf.writeVarInt(msg.z);
        buf.writeUtf(msg.teamName);
        buf.writeVarInt(msg.teamColor);
        buf.writeBoolean(msg.isAdd);
        buf.writeBoolean(msg.replace);
        buf.writeBoolean(msg.forceLoaded);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClaimedChunkPolygon.addToQueue(dim, x, z, teamName, teamColor, isAdd, replace, forceLoaded);
        });
        ctx.get().setPacketHandled(true);
    }
}
