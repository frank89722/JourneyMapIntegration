package frankv.jmi.waypointmessage;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import static frankv.jmi.JMI.clientConfig;

public class WaypointChatMessage {
    private static Minecraft mc = Minecraft.getInstance();
    private static BlockPos prevBlock = null;

    public static void onRightClickOnBlock(BlockPos blockPos, ItemStack holdingItemStack) {
        if (clientConfig.getWaypointMessageBlocks().isEmpty()) return;
        if (clientConfig.getWaypointMessageEmptyHandOnly() && !holdingItemStack.equals(ItemStack.EMPTY)) return;

        var block = mc.level.getBlockState(blockPos).getBlock();

        if (!checkBlockTags(block) || blockPos.equals(prevBlock) || !mc.player.isShiftKeyDown()) return;

        prevBlock = blockPos;

        var name = block.getName().getString();
        var msg = String.format("[JMI] [name:\"%s\", x:%d, y:%d, z:%d]", name, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        mc.player.connection.handleChat(new ClientboundChatPacket(new TextComponent(msg), ChatType.CHAT, Util.NIL_UUID));

    }

    private static boolean checkBlockTags(Block block) {
        return block.defaultBlockState().getTags().anyMatch(e -> clientConfig.getWaypointMessageBlocks().contains('#' + e.location().toString())) ||
                clientConfig.getWaypointMessageBlocks().contains(block.getName().toString());
    }
}
