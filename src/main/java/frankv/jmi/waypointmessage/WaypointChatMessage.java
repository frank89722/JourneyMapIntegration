package frankv.jmi.waypointmessage;

import frankv.jmi.JMI;
import frankv.jmi.mixin.WaypointParserInvoker;
import journeymap.client.waypoint.WaypointParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WaypointChatMessage {
    private static Minecraft mc = Minecraft.getInstance();
    private static BlockPos prevBlock = null;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseClick(PlayerInteractEvent.RightClickBlock event) {
        if (JMI.CLIENT_CONFIG.getWaypointMessageBlocks().isEmpty()) return;
        if (JMI.CLIENT_CONFIG.getWaypointMessageEmptyHandOnly()) {
            if (!event.getItemStack().equals(ItemStack.EMPTY)) return;
        }

        var blockPos = event.getHitVec().getBlockPos();
        var block = mc.level.getBlockState(blockPos).getBlock();

        if (!checkBlockTags(block) || blockPos.equals(prevBlock) || !mc.player.isShiftKeyDown()) return;

        var name = block.getName().getString();
        var msg = String.format("[JMI] [name:\"%s\", x:%d, y:%d, z:%d]", name, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        mc.player.sendMessage(WaypointParserInvoker.addWaypointMarkup(msg, WaypointParser.getWaypointStrings(msg)), mc.player.getUUID());

        prevBlock = blockPos;
        event.setCanceled(true);
    }

    private static boolean checkBlockTags(Block block){
        return block.getTags().stream().anyMatch(e -> JMI.CLIENT_CONFIG.getWaypointMessageBlocks().contains('#' + e.toString())) ||
                JMI.CLIENT_CONFIG.getWaypointMessageBlocks().contains(block.getRegistryName().toString());
    }
}
