package frankv.jmi.ftbchunks.network;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import frankv.jmi.ftbchunks.ClaimedChunkPolygon;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClaimedData {
    private ResourceLocation dim;
    private int x, z;
    private String teamName;
    private int teamColor;
    private boolean isAdd;
    private boolean replace;

    public PacketClaimedData(ClaimedChunk chunk, boolean isAdd, boolean replace) {
        this.x = chunk.getPos().getChunkPos().x;
        this.z = chunk.getPos().getChunkPos().z;
        this.teamName = chunk.getTeamData().getTeam().getDisplayName();
        System.out.println(this.teamName);
        this.teamColor = chunk.getTeamData().getTeam().getColor();
        this.isAdd = isAdd;
        this.dim = chunk.getPos().dimension.location();
        this.replace = replace;
    }

    public PacketClaimedData(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace) {
        this.x = x;
        this.z = z;
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.isAdd = isAdd;
        this.dim = dim;
        this.replace = replace;
    }

    public static PacketClaimedData read(PacketBuffer buf){
        return new PacketClaimedData(buf.readResourceLocation(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void write(PacketClaimedData msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.dim);
        buf.writeVarInt(msg.x);
        buf.writeVarInt(msg.z);
        buf.writeUtf(msg.teamName);
        buf.writeVarInt(msg.teamColor);
        buf.writeBoolean(msg.isAdd);
        buf.writeBoolean(msg.replace);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClaimedChunkPolygon.addToQueue(dim, x, z, teamName, teamColor, isAdd, replace);
        });
        ctx.get().setPacketHandled(true);
    }
}
