package me.frankv.jmi.waypointmessage;

import me.frankv.jmi.JMI;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
                name, blockPos.getX(), blockPos.getY(), blockPos.getZ(), mc.player.level().dimension().identifier());

        mc.player.connection.handleSystemChat(new ClientboundSystemChatPacket(Component.literal(msg), false));
    }

    private static boolean checkBlockTags(Block block) {
        final var blocks = JMI.getClientConfig().getWaypointMessageBlocks();
        final var holder = BuiltInRegistries.BLOCK.wrapAsHolder(block);
        return holder.tags().anyMatch(e -> blocks.contains('#' + e.location().toString())) ||
                blocks.contains(BuiltInRegistries.BLOCK.getKey(block).toString());
    }
}
