package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class BedBreaker extends Module {

    public Setting<Float> radius = register(new Setting<>("Radius", 0.9f, 0.5f, 4.0f));
    private BlockPos targetBed = null;

    public BedBreaker() {
        super("BedBreaker", Category.PLAYER, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (targetBed == null) {
            targetBed = findNearbyBed();
        }

        if (targetBed != null) {
            breakBed(targetBed);
        }
    }

    private BlockPos findNearbyBed() {
        BlockPos playerPos = mc.thePlayer.getPosition();
        int r = (int) Math.ceil(radius.getValue());

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    double dist = mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (dist > radius.getValue()) continue;

                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    if (block instanceof BlockBed) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private void breakBed(BlockPos pos) {
        PlayerControllerMP controller = mc.playerController;

        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (!(block instanceof BlockBed)) {
            targetBed = null;
            controller.resetBlockRemoving();
            return;
        }
        controller.onPlayerDamageBlock(pos, EnumFacing.UP);
        mc.thePlayer.swingItem();
    }

    @Override
    public void onDisable() {
        targetBed = null;
        if (mc.playerController != null) {
            mc.playerController.resetBlockRemoving();
        }
    }
}