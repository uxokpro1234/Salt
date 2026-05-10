package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import me.uxokpro1234.salt.event.events.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoClip extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Setting<Float> speed = register(new Setting<>("Speed", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> ascend = register(new Setting<>("AscendWithJump", true));
    public Setting<Boolean> descend = register(new Setting<>("DescendWithSneak", true));

    public NoClip() {
        super("NoClip", Category.PLAYER, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = true;
            mc.thePlayer.onGround = false;
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = false;
        }
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        mc.thePlayer.motionY = 0;
        double motionY = 0;

        if (ascend.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) motionY = speed.getValue();
        if (descend.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) motionY = -speed.getValue();

        float forward = (float) event.getMoveForward();
        float strafe = (float) event.getMoveStrafe();
        float yaw = mc.thePlayer.rotationYaw;
        double rad = Math.toRadians(yaw);
        double motionX = (forward * -Math.sin(rad) + strafe * Math.cos(rad)) * speed.getValue();
        double motionZ = (forward * Math.cos(rad) + strafe * Math.sin(rad)) * speed.getValue();
        mc.thePlayer.motionX = motionX;
        mc.thePlayer.motionY = motionY;
        mc.thePlayer.motionZ = motionZ;
        mc.thePlayer.noClip = true;
        mc.thePlayer.onGround = false;

        event.setMoveForward(motionX);
        event.setMoveStrafe(motionZ);
    }
}