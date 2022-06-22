package frankv.jmi.waypointmessage;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.time.Instant;
import java.util.Optional;

import static frankv.jmi.JMI.clientConfig;

public class WaypointChatMessage {
    private static Minecraft mc = Minecraft.getInstance();
    private static BlockPos prevBlock = null;

    public static void onRightClickOnBlock(BlockPos blockPos, ItemStack holdingItemStack) {
        if (clientConfig.getWaypointMessageBlocks().isEmpty()) return;
        if (clientConfig.getWaypointMessageEmptyHandOnly() && !holdingItemStack.equals(ItemStack.EMPTY)) return;

        final var block = mc.level.getBlockState(blockPos).getBlock();

        if (!checkBlockTags(block) || blockPos.equals(prevBlock) || !mc.player.isShiftKeyDown()) return;

        prevBlock = blockPos;

        final var name = block.getName().getString();
        final var msg = String.format("[name:\"%s\", x:%d, y:%d, z:%d]", name, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        mc.player.connection.handlePlayerChat(new ClientboundPlayerChatPacket(Component.literal(msg), Optional.of(Component.literal(msg)), 0, new ChatSender(Util.NIL_UUID, Component.literal("JMI")), Instant.now(), null));
    }

    private static boolean checkBlockTags(Block block) {
        final var blockStrLen = block.toString().length();
        System.out.println(block.toString().substring(6, blockStrLen-1));
        return block.defaultBlockState().getTags()
                .anyMatch(e -> clientConfig.getWaypointMessageBlocks().contains('#' + e.location().toString())) ||
                clientConfig.getWaypointMessageBlocks().contains(block.toString().substring(6, blockStrLen-1));
    }
}
