package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class BoatFly extends Module {

    public Setting<Float> speed = register(new Setting<>("Speed", 1.2f, 0.1f, 5.0f));
    public Setting<Float> ySpeed = register(new Setting<>("YSpeed", 0.5f, 0.0f, 2.0f));
    public Setting<Boolean> glide = register(new Setting<>("Glide", true));

    public BoatFly() {
        super("BoatFly", Category.MOVEMENT, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ridingEntity instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat) mc.thePlayer.ridingEntity;
            boat.motionX *= 0.5;
            boat.motionZ *= 0.5;
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction packet = event.getPacket();

            if (mc.thePlayer != null && mc.thePlayer.ridingEntity instanceof EntityBoat) {
                if (packet.getAction() == C0BPacketEntityAction.Action.START_SNEAKING
                        || packet.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!(mc.thePlayer.ridingEntity instanceof EntityBoat)) return;

        EntityBoat boat = (EntityBoat) mc.thePlayer.ridingEntity;
        boat.motionY = 0;
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double baseX = -Math.sin(yaw) * forward + Math.cos(yaw) * strafe;
        double baseZ = Math.cos(yaw) * forward + Math.sin(yaw) * strafe;
        double multiplier = speed.getValue();
        boat.motionX += baseX * multiplier - boat.motionX;
        boat.motionZ += baseZ * multiplier - boat.motionZ;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            boat.motionY = ySpeed.getValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            boat.motionY = -ySpeed.getValue();
        } else if (glide.getValue()) {
            boat.motionY = -0.03;
        }

        //prevent desync
        boat.isAirBorne = true;

        if (mc.thePlayer.ticksExisted % 20 == 0) {
            mc.thePlayer.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer(mc.thePlayer.onGround));
        }
    }
}