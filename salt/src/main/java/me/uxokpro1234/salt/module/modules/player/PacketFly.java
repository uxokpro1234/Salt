package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import me.uxokpro1234.salt.util.TimerUtil;
import me.uxokpro1234.salt.event.events.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class PacketFly extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Setting<Float> speed = register(new Setting<>("Speed", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> ascend = register(new Setting<>("AscendWithJump", true));
    public Setting<Boolean> descend = register(new Setting<>("DescendWithSneak", true));
    private final TimerUtil timer = new TimerUtil();

    public PacketFly() {
        super("PacketFly", Category.MOVEMENT, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        double motionY = 0;
        if (ascend.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) motionY += speed.getValue();
        if (descend.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) motionY -= speed.getValue();

        float forward = (float) event.getMoveForward();
        float strafe = (float) event.getMoveStrafe();
        float yaw = mc.thePlayer.rotationYaw;
        double rad = Math.toRadians(yaw);
        double motionX = (forward * -Math.sin(rad) + strafe * Math.cos(rad)) * speed.getValue();
        double motionZ = (forward * Math.cos(rad) + strafe * Math.sin(rad)) * speed.getValue();

        mc.thePlayer.motionX = motionX;
        mc.thePlayer.motionY = motionY;
        mc.thePlayer.motionZ = motionZ;
        
        if (timer.hasReached(100)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + motionX, mc.thePlayer.posY + motionY, mc.thePlayer.posZ + motionZ, mc.thePlayer.onGround));
            timer.reset();
        }
        System.out.println("packet");
        event.setMoveForward(motionX);
        event.setMoveStrafe(motionZ);
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
        }
    }
}