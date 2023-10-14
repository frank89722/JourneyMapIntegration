package me.frankv.jmi.waypointmessage;

import me.frankv.jmi.JMI;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;


public class WaypointChatMessage {
    private static Minecraft mc = Minecraft.getInstance();
    private static BlockPos prevBlock = null;

    public static void onRightClickOnBlock(BlockPos blockPos, ItemStack holdingItemStack) {
        if (JMI.getClientConfig().getWaypointMessageBlocks().isEmpty()) return;
        if (JMI.getClientConfig().getWaypointMessageEmptyHandOnly() && !holdingItemStack.equals(ItemStack.EMPTY)) return;

        final var block = mc.level.getBlockState(blockPos).getBlock();

        if (!checkBlockTags(block) || blockPos.equals(prevBlock) || !mc.player.isShiftKeyDown()) return;

        prevBlock = blockPos;

        final var name = block.getName().getString();
        final var msg = String.format("[name:\"%s\", x:%d, y:%d, z:%d, dim:%s]",
                name, blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player.level().dimension().location());

        mc.player.connection.handleSystemChat(new ClientboundSystemChatPacket(Component.literal(msg), false));
    }

    private static boolean checkBlockTags(Block block) {
        final var blockStrLen = block.toString().length();
        return block.defaultBlockState().getTags()
                .anyMatch(e -> JMI.getClientConfig().getWaypointMessageBlocks().contains('#' + e.location().toString())) ||
                JMI.getClientConfig().getWaypointMessageBlocks().contains(block.toString().substring(6, blockStrLen-1));
    }
}
