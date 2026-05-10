package me.uxokpro1234.salt.module.modules.movement;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {

    public enum Mode {
        VANILLA,
        MULTI
    }

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.VANILLA));

    public Sprint() {
        super("Sprint", Category.MOVEMENT, Keyboard.KEY_P);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null) return;

        boolean canSprint = !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && mc.thePlayer.onGround; // ✅ 1.8.9 SAFE

        if (!canSprint) {
            mc.thePlayer.setSprinting(false);
            return;
        }

        switch (mode.getValue()) {

            case VANILLA:
                if (mc.thePlayer.movementInput.moveForward > 0) {
                    mc.thePlayer.setSprinting(true);
                }
                break;

            case MULTI:
                boolean moving =
                        mc.thePlayer.movementInput.moveForward != 0
                                || mc.thePlayer.movementInput.moveStrafe != 0;

                if (moving) {
                    mc.thePlayer.setSprinting(true);
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.setSprinting(false);
        }
    }
}