package me.uxokpro1234.salt.module.modules.player;
import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Op2525 extends Module {

    public Setting<Boolean> no = register(new Setting<>("NO", false));

    public Op2525() {
        super("Op2525", Category.PLAYER, Keyboard.KEY_NONE);
    }



    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow && ((EntityArrow) event.entity).shootingEntity == mc.thePlayer) {

            EntityArrow arrow = (EntityArrow) event.entity;

            arrow.motionX *= 1E3;
            arrow.motionY *= 1E3;
            arrow.motionZ *= 1E3;
            arrow.setIsCritical(true);

        }
    }
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {

        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (event.getPacket() instanceof C07PacketPlayerDigging && no.getValue()) {

            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.getPacket();

            if (packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.bow) {
                mc.thePlayer.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 2.9E7, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2.9E7, mc.thePlayer.posZ, false));
            }
        }
    }
}