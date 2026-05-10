package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class SafeWalk extends Module {
    public enum mode {STATIC, SHIFT, VELOCITY, PREDICTIVE, BOOST, MULTI_LAYER, CUSTOM}
    public Setting<mode> Mode = register(new Setting<>("Mode", mode.PREDICTIVE));

    public Setting<Double> SLOW_FACTOR = register(new Setting<>("SlowFactor", 0.1, 0.01, 1.0));
    public Setting<Double> BOOST_FACTOR = register(new Setting<>("BoostFactor", 0.3, 0.01, 1.0));
    public Setting<Double> CUSTOM_SLOW = register(new Setting<>("CustomSlow", 0.2, 0.01, 1.0));
    public Setting<Double> CUSTOM_Y = register(new Setting<>("CustomY", -0.05, -1.0, 0.0));
    public Setting<Integer> PREDICT_DISTANCE = register(new Setting<>("PredictDistance", 0, 0, 5));
    public Setting<Integer> CHECK_LAYERS = register(new Setting<>("CheckLayers", 1, 1, 5));
    public Setting<Boolean> JUMP_ASSIST = register(new Setting<>("JumpAssist", true));
    public Setting<Boolean> AUTO_SNEAK = register(new Setting<>("AutoSneak", true));
    public Setting<Boolean> SET_VELOCITY_PACKET = register(new Setting<>("UseVelocityPacket", false));
    public Setting<Boolean> USE_VELOCITY_PACKET = register(new Setting<>("UseVelocityPacketCustom", false));

    public SafeWalk() {
        super("SafeWalk", Category.MOVEMENT, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!mc.thePlayer.onGround) return;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY - 1.0;
        double z = mc.thePlayer.posZ;

        boolean overAir = mc.theWorld.isAirBlock(new net.minecraft.util.BlockPos(x, y, z)
        );

        switch (Mode.getValue()) {

            case STATIC:
                if (overAir) {
                    double slowFactor = SLOW_FACTOR.getValue();
                    mc.thePlayer.motionX *= slowFactor;
                    mc.thePlayer.motionZ *= slowFactor;

                    mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, -0.01);

                    if (AUTO_SNEAK.getValue()) {
                        mc.thePlayer.setSneaking(true);
                    }
                } else if (AUTO_SNEAK.getValue()) {
                    mc.thePlayer.setSneaking(false);
                }
                break;

            case VELOCITY:
                if (overAir) {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;

                    if (mc.thePlayer.motionY > 0) mc.thePlayer.motionY *= 0.5;
                    if (mc.thePlayer.motionY < 0) mc.thePlayer.motionY = Math.max(mc.thePlayer.motionY, -0.1);

                    if (SET_VELOCITY_PACKET.getValue()) {
                        mc.thePlayer.setVelocity(0, 0, 0); // optional, server-side freeze
                    }

                    if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(true);
                } else if (AUTO_SNEAK.getValue()) {
                    mc.thePlayer.setSneaking(false);
                }
                break;

            case SHIFT:
                mc.thePlayer.setSneaking(overAir);
                break;

            case PREDICTIVE:
                int lookAhead = PREDICT_DISTANCE.getValue();
                boolean dangerAhead = false;
                for (int i = 1; i <= lookAhead; i++) {
                    double futureX = mc.thePlayer.posX + mc.thePlayer.motionX * i;
                    double futureZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * i;
                    if (mc.theWorld.isAirBlock(new BlockPos(Math.floor(futureX), Math.floor(mc.thePlayer.posY - 1), Math.floor(futureZ)))) {
                        dangerAhead = true;
                        break;
                    }
                }
                if (dangerAhead) {
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;
                    if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(true);
                } else if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(false);
                break;

            case BOOST:
                if (overAir) {
                    double boostSpeed = BOOST_FACTOR.getValue(); // configurable, 0.1-1.0
                    mc.thePlayer.motionX *= 1 - boostSpeed;
                    mc.thePlayer.motionZ *= 1 - boostSpeed;
                    mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, 0);

                    if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(true);

                    if (JUMP_ASSIST.getValue() && mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                } else if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(false);
                break;

            case MULTI_LAYER:
                boolean dangerBelow = false;
                int checkLayers = CHECK_LAYERS.getValue(); // e.g., 2-5
                for (int i = 1; i <= checkLayers; i++) {
                    if (mc.theWorld.isAirBlock(new BlockPos(Math.floor(mc.thePlayer.posX),
                            Math.floor(mc.thePlayer.posY - i),
                            Math.floor(mc.thePlayer.posZ)))) {
                        dangerBelow = true;
                        break;
                    }
                }
                if (dangerBelow) {
                    mc.thePlayer.motionX *= 0.1;
                    mc.thePlayer.motionZ *= 0.1;
                    if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(true);
                } else if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(false);
                break;

            case CUSTOM:
                if (overAir) {
                    double customFactor = CUSTOM_SLOW.getValue();
                    mc.thePlayer.motionX *= customFactor;
                    mc.thePlayer.motionZ *= customFactor;

                    double customY = CUSTOM_Y.getValue();
                    mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, customY);

                    if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(true);

                    if (USE_VELOCITY_PACKET.getValue()) mc.thePlayer.setVelocity(0, 0, 0);
                } else if (AUTO_SNEAK.getValue()) mc.thePlayer.setSneaking(false);
                break;
        }
    }
}