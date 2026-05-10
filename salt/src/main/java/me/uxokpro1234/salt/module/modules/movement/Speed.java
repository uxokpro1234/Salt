package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {

    public enum Mode { VANILLA_PLUS, STRAFE, BHOP, CONTROLLED }

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.VANILLA_PLUS));
    public Setting<Float> speed = register(new Setting<>("Speed", 1.1f, 1f, 3f));
    public Setting<Boolean> autoJump = register(new Setting<>("AutoJump", true));
    public Setting<Boolean> autoSprint = register(new Setting<>("AutoSprint", true));
    public Setting<Float> hitBoost = register(new Setting<>("HitBoost", 1.3f, 1f, 2f));
    public Setting<Integer> hitBoostTicks = register(new Setting<>("HitTicks", 10, 1, 30));
    public Setting<Boolean> frictionControl = register(new Setting<>("Friction", true));

    private int hitTimer = 0;
    private float boostMultiplier = 1f;

    public Speed() {
        super("Speed", Category.MOVEMENT, Keyboard.KEY_NONE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        /*if (mc.thePlayer != null) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            boostMultiplier = 1f;
            hitTimer = 0;
        }*/
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        /*if (event.entity == mc.thePlayer && event.source != DamageSource.outOfWorld) {
            boostMultiplier = hitBoost.getValue();
            hitTimer = hitBoostTicks.getValue();
        }*/
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        /*if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!isMoving()) return;

        // HitBoost decay
        if (hitTimer > 0) {
            hitTimer--;
            if (hitTimer == 0) boostMultiplier = 1f;
        }

        if (autoSprint.getValue()) mc.thePlayer.setSprinting(true);

        switch (mode.getValue()) {
            case VANILLA_PLUS: vanillaPlus(); break;
            case STRAFE: strafe(); break;
            case BHOP: bhop(); break;
            case CONTROLLED: controlled(); break;
        }*/
    }

    /* ================= MODES ================= */

    private void vanillaPlus() {
        double baseSpeed = 0.1 * speed.getValue() * boostMultiplier;
        move(baseSpeed);
    }

    private void strafe() {
        if (mc.thePlayer.onGround && autoJump.getValue()) mc.thePlayer.jump();
        move(0.13 * speed.getValue() * boostMultiplier);
    }

    private void bhop() {
        if (mc.thePlayer.onGround) {
            if (autoJump.getValue()) mc.thePlayer.jump();
            move(0.18 * speed.getValue() * boostMultiplier);
        } else move(0.13 * speed.getValue() * boostMultiplier);
    }

    private void controlled() {
        move(0.1 * speed.getValue() * boostMultiplier);
    }

    /* ================= HELPERS ================= */

    private void move(double base) {
        double friction = frictionControl.getValue() ? getFriction() : 1.0;
        double yaw = mc.thePlayer.rotationYaw;
        double forward = mc.thePlayer.moveForward;
        double strafe = mc.thePlayer.moveStrafing;

        if (forward == 0 && strafe == 0) return;

        // Diagonal correction
        if (forward != 0) {
            if (strafe > 0) yaw += (forward > 0 ? -45 : 45);
            else if (strafe < 0) yaw += (forward > 0 ? 45 : -45);
            strafe = 0;
            forward = forward > 0 ? 1 : -1;
        }

        double rad = Math.toRadians(yaw + 90.0F);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        mc.thePlayer.motionX = (forward * base * cos + strafe * base * sin) * friction;
        mc.thePlayer.motionZ = (forward * base * sin - strafe * base * cos) * friction;
    }

    private boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    private double getFriction() {
        if (mc.thePlayer.onGround) return 0.91;
        else return 0.98; // air friction
    }
}