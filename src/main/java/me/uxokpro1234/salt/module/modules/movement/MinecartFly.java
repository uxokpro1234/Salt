package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.entity.item.EntityMinecart;
import org.lwjgl.input.Keyboard;

public class MinecartFly extends Module {

    public Setting<Float> speed = register(new Setting<>("Speed", 0.6f, 0.05f, 5.0f));
    public Setting<Float> ySpeed = register(new Setting<>("YSpeed", 0.3f, 0.0f, 1.0f));
    public Setting<Boolean> glide = register(new Setting<>("Glide", true));

    public MinecartFly() {
        super("MinecartFly", Category.MOVEMENT, Keyboard.KEY_NONE);
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (!(mc.thePlayer.ridingEntity instanceof EntityMinecart))
            return;

        EntityMinecart cart = (EntityMinecart) mc.thePlayer.ridingEntity;

        float forward = mc.thePlayer.movementInput.moveForward;
        float strafe = mc.thePlayer.movementInput.moveStrafe;

        float yaw = mc.thePlayer.rotationYaw;

        if (forward != 0 || strafe != 0) {

            if (forward != 0) {
                if (strafe > 0)
                    yaw += (forward > 0 ? -45 : 45);
                else if (strafe < 0)
                    yaw += (forward > 0 ? 45 : -45);

                strafe = 0;

                forward = forward > 0 ? 1 : -1;
            }

            double rad = Math.toRadians(yaw + 90.0F);

            cart.motionX = forward * speed.getValue() * Math.cos(rad)
                    + strafe * speed.getValue() * Math.sin(rad);

            cart.motionZ = forward * speed.getValue() * Math.sin(rad)
                    - strafe * speed.getValue() * Math.cos(rad);
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            cart.motionY = ySpeed.getValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            cart.motionY = -ySpeed.getValue();
        } else if (glide.getValue()) {
            cart.motionY = -0.03;
        }
    }
}