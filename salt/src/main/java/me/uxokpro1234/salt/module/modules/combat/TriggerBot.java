package me.uxokpro1234.salt.module.modules.combat;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TriggerBot extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();

    public enum Mode {CROSSHAIR, CLOSEST, SWITCH, LOWEST_HEALTH}
    public enum AimStyle {NONE, SMOOTH, HUMANIZED}

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.CROSSHAIR));
    public Setting<AimStyle> aimStyle = register(new Setting<>("AimStyle", AimStyle.NONE));
    public Setting<Integer> minDelay = register(new Setting<>("MinDelay", 50, 0, 500));
    public Setting<Integer> maxDelay = register(new Setting<>("MaxDelay", 120, 0, 500));
    public Setting<Float> range = register(new Setting<>("Range", 4.5f, 1f, 6f));
    public Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));
    public Setting<Boolean> ignoreInvisible = register(new Setting<>("IgnoreInvisible", true));

    private long lastAttack = 0;
    private int switchIndex = 0;

    public TriggerBot() {
        super("TriggerBot", Category.COMBAT, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        lastAttack = 0;
        switchIndex = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        int delay = minDelay.getValue() + random.nextInt(maxDelay.getValue() - minDelay.getValue() + 1);
        if (System.currentTimeMillis() - lastAttack < delay) return;

        Entity target = selectTarget();

        if (target != null) {
            if (aimStyle.getValue() != AimStyle.NONE) {
                smoothAimAt(target);
            }

            if (random.nextFloat() < 0.95f) {
                mc.playerController.attackEntity(mc.thePlayer, target);
                mc.thePlayer.swingItem();
            }

            lastAttack = System.currentTimeMillis();
        }
    }

    private Entity selectTarget() {
        List<EntityPlayer> players = mc.theWorld.playerEntities;
        players.removeIf(p -> p == mc.thePlayer ||
                (playersOnly.getValue() && !(p instanceof EntityPlayer)) ||
                (ignoreInvisible.getValue() && p.isInvisible()) ||
                mc.thePlayer.getDistanceToEntity(p) > range.getValue());

        if (players.isEmpty()) return null;

        switch (mode.getValue()) {
            case CROSSHAIR:
                MovingObjectPosition mop = mc.objectMouseOver;
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    Entity e = mop.entityHit;
                    if (e != null && players.contains(e)) return e;
                }
                return null;
            case CLOSEST:
                return players.stream()
                        .min(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity))
                        .orElse(null);
            case SWITCH:
                Entity target = players.get(switchIndex % players.size());
                switchIndex++;
                return target;
            case LOWEST_HEALTH:
                return players.stream()
                        .min(Comparator.comparingDouble(EntityPlayer::getHealth))
                        .orElse(null);
        }
        return null;
    }

    private void smoothAimAt(Entity target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dy = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx*dx + dz*dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        mc.thePlayer.rotationYaw += (yaw - mc.thePlayer.rotationYaw) * 0.2f;
        mc.thePlayer.rotationPitch += (pitch - mc.thePlayer.rotationPitch) * 0.2f;
        mc.thePlayer.rotationYaw += random.nextFloat() - 0.5f;
        mc.thePlayer.rotationPitch += random.nextFloat() - 0.5f;
    }
}