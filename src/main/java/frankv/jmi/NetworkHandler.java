package frankv.jmi;

import frankv.jmi.ftbchunks.network.PacketClaimedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel CHANNEL_INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void register() {
        CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(JMI.MODID, "jmi"),
                () -> "1.0",
                s -> true,
                s -> true);

        CHANNEL_INSTANCE.messageBuilder(PacketClaimedData.class, nextID())
                .encoder(PacketClaimedData::write)
                .decoder(PacketClaimedData::read)
                .consumer(PacketClaimedData::handle)
                .add();
    }

    public static void sendFTBToClient(Object packet, ServerPlayerEntity player) {
        CHANNEL_INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendFTBToClient(Object packet, RegistryKey<World> dim) {
        CHANNEL_INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), packet);
    }

    public static void sendFTBToClient(Object packet) {
        CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL_INSTANCE.sendToServer(packet);
    }
}
