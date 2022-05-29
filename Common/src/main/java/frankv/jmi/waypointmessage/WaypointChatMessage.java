package frankv.jmi.waypointmessage;

import frankv.jmi.config.IClientConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class WaypointChatMessage {
    private Minecraft mc = Minecraft.getInstance();
    private BlockPos prevBlock = null;
    private IClientConfig clientConfig;

    public WaypointChatMessage(IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public void onMouseClick(BlockPos blockPos, ItemStack holdingItemstack) {
        if (clientConfig.getWaypointMessageBlocks().isEmpty()) return;
        if (clientConfig.getWaypointMessageEmptyHandOnly() && !holdingItemstack.equals(ItemStack.EMPTY)) return;

        var block = mc.level.getBlockState(blockPos).getBlock();

        if (!checkBlockTags(block) || blockPos.equals(prevBlock) || !mc.player.isShiftKeyDown()) return;

        prevBlock = blockPos;

        var name = block.getName().getString();
        var msg = String.format("[JMI] [name:\"%s\", x:%d, y:%d, z:%d]", name, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        mc.player.connection.handleChat(new ClientboundChatPacket(new TextComponent(msg), ChatType.CHAT, Util.NIL_UUID));
//        mc.player.sendMessage(WaypointParserInvoker.addWaypointMarkup(msg, WaypointParser.getWaypointStrings(msg)), mc.player.getUUID());
//        event.setCanceled(true);
//        return Optional.of(msg);
    }

    private boolean checkBlockTags(Block block) {
        return block.defaultBlockState().getTags().anyMatch(e -> clientConfig.getWaypointMessageBlocks().contains('#' + e.location().toString())) ||
                clientConfig.getWaypointMessageBlocks().contains(block.getName().toString());
    }
}
