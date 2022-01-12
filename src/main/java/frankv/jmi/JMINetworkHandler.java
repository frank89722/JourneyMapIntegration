package frankv.jmi;

//import frankv.jmi.ftbchunks.network.PacketClaimedData;
import frankv.jmi.ftbchunks.network.PacketClaimedData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class JMINetworkHandler {
    public static SimpleChannel CHANNEL_INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void register() {
        CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(JMI.MODID, "jmi"),
                () -> "1.1",
                s -> true,
                s -> true);

        CHANNEL_INSTANCE.messageBuilder(PacketClaimedData.class, nextID())
                .encoder(PacketClaimedData::write)
                .decoder(PacketClaimedData::read)
                .consumer(PacketClaimedData::handle)
                .add();
    }

    public static void sendFTBToClient(Object packet, ServerPlayer player) {
        CHANNEL_INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendFTBToClient(Object packet, ResourceKey<Level> dim) {
        CHANNEL_INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), packet);
    }

    public static void sendFTBToClient(Object packet) {
        CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL_INSTANCE.sendToServer(packet);
    }
}
