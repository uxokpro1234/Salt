package me.uxokpro1234.salt.module.modules.combat;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AimAssist extends Module {

    public Setting<Float> range = register(new Setting<>("Range", 6f, 0f, 50f));
    public Setting<Float> smoothness = register(new Setting<>("Smoothness", 0.1f, 0.1f, 1.2f));
    public Setting<Float> fov = register(new Setting<>("Fov", 90.0f, 45f, 180f));
    public Setting<Boolean> targetMobs = register(new Setting<>("TargetMobs", true));


    public AimAssist() {
        super("AimAssist", Category.COMBAT, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mc.thePlayer == null || mc.theWorld == null) return;

        EntityLivingBase target = findTarget();
        if (target == null) return;

        aimAt(target);
    }

    private EntityLivingBase findTarget() {

        EntityPlayerSP player = mc.thePlayer;
        EntityLivingBase best = null;
        double closest = range.getValue();

        for (Object obj : mc.theWorld.loadedEntityList) {

            if (!(obj instanceof EntityLivingBase)) continue;

            EntityLivingBase entity = (EntityLivingBase) obj;

            if (entity == player) continue;
            if (entity.isDead) continue;
            if (!targetMobs.getValue() && !(entity instanceof EntityPlayer)) continue;

            double distance = player.getDistanceToEntity(entity);
            if (distance > range.getValue()) continue;

            if (!player.canEntityBeSeen(entity)) continue;

            float angle = getAngleDifference(entity);
            if (angle > fov.getValue()) continue;

            if (distance < closest) {
                closest = distance;
                best = entity;
            }
        }

        return best;
    }


    private float getAngleDifference(Entity entity) {

        double dx = entity.posX - mc.thePlayer.posX;
        double dz = entity.posZ - mc.thePlayer.posZ;

        float yawToEntity = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        return Math.abs(wrapAngleTo180(yawToEntity - mc.thePlayer.rotationYaw));
    }

    private float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }


    public void aimAt(Entity target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dy = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        float yawDiff = wrapAngleTo180(targetYaw - mc.thePlayer.rotationYaw);
        float pitchDiff = wrapAngleTo180(targetPitch - mc.thePlayer.rotationPitch);
        float maxTurn = 25f;
        float yawStep = clamp(yawDiff, -maxTurn, maxTurn);
        float pitchStep = clamp(pitchDiff, -maxTurn, maxTurn);
        float smoothFactor = smoothness.getValue();
        mc.thePlayer.rotationYaw += yawStep / smoothFactor;
        mc.thePlayer.rotationPitch += pitchStep / smoothFactor;
        mc.thePlayer.rotationPitch = clamp(mc.thePlayer.rotationPitch, -90f, 90f);
    }
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}