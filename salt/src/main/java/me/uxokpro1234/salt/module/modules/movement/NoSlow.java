package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.event.events.PlayerMoveEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {


    public enum Mode {VANILLA, MATRIX, PACKET}

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.VANILLA));
    public Setting<Float> slowf = register(new Setting<>("SlowF", 1.2f, 1.0f, 1.2f));
    public Setting<Float> slows = register(new Setting<>("SlowS", 1.2f, 0.9f, 1.2f));
    public Setting<Boolean> food = register(new Setting<>("Food", true));
    public Setting<Boolean> bow = register(new Setting<>("Bow", true));
    public Setting<Boolean> all = register(new Setting<>("AllItems", false));
    public Setting<Boolean> aac = register(new Setting<>("AAC", false));
    public Setting<Boolean> strict = register(new Setting<>("Strict", false));
    public Setting<Boolean> grim = register(new Setting<>("Grim", false));


    public NoSlow() {
        super("NoSlow", Category.MOVEMENT, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isEnabled()) return;

        ItemStack held = event.getPlayer().getHeldItem();
        if (held == null) return;

        boolean apply = mc.thePlayer.isUsingItem() && (
                (food.getValue() && mc.thePlayer.getItemInUse().getItem() instanceof ItemFood) ||
                        (bow.getValue() && mc.thePlayer.getItemInUse().getItem() instanceof ItemBow) ||
                        all.getValue()
        );

        if (!apply) return;

        switch (mode.getValue()) {
            case VANILLA:
                float forward = (float) event.getMoveForward();
                float strafe = (float) event.getMoveStrafe();

                if (mc.thePlayer.isSprinting()) {
                    forward *= 6.5f;
                    strafe *= 6.5f;
                } else {
                    forward *= 5;
                    strafe *= 5;
                }

                event.setMoveForward(forward);
                event.setMoveStrafe(strafe);
                break;
            case MATRIX:
                event.setMoveForward(event.getMoveForward() * slowf.getValue());
                event.setMoveStrafe(event.getMoveStrafe() * slows.getValue());
                break;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (event.phase != TickEvent.Phase.START) return;

        if (mc.thePlayer.isUsingItem() && mode.getValue() == Mode.PACKET) {

            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
        }
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;

        if (strict.getValue() && mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        if (aac.getValue() && mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
    }
    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S2FPacketSetSlot && grim.getValue()) {
            event.setCanceled(true);
        }
    }
}