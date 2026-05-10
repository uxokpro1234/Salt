package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.event.events.ReachEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Reach extends Module {

    public enum ReachMode {
        VANILLA,       // 3.0 blocks
        LEGIT,         // AC-safe ~4.3–4.5 blocks
        DYNAMIC        // Slight randomization for realism
    }

    // ================= SETTINGS =================
    public Setting<ReachMode> mode = register(new Setting<>("Mode", ReachMode.LEGIT));
    public Setting<Float> reach = register(new Setting<>("Reach", 3.0f, 3.0f, 4.0f));

    //public Setting<Float> minReach = register(new Setting<>("MinReach", 3.0f, 3.0f, 4.5f));
    //public Setting<Float> maxReach = register(new Setting<>("MaxReach", 4.3f, 3.0f, 4.5f));
    public Setting<Boolean> onlyOnAttack = register(new Setting<>("OnlyAttack", true));
    public Setting<Boolean> randomize = register(new Setting<>("Randomize", true));

    public Reach() {
        super("Reach", Category.COMBAT, Keyboard.CHAR_NONE);
    }

    @SubscribeEvent
    public void onReach(ReachEvent event) {
        if (!isEnabled() || mc.thePlayer == null) return;

        if (isEnabled()) {
            event.setReach(reach.getValue());
        }else{
            event.setReach(3.0d);
        }
    }



    private float calculateReach() {
        float reach = 3.0f;

        switch (mode.getValue()) {
            case VANILLA:
                reach = 3.0f;
                break;

            case LEGIT:
                reach = 3.2f;
                if (randomize.getValue()) {
                    reach += (float) ((Math.random() - 0.5) * 0.1f);
                }
                break;

            case DYNAMIC:
                reach = 3.5f + (float)(Math.random() * 0.8f);
                break;
        }
        return 0;
        //return clamp(reach, minReach.getValue(), maxReach.getValue());
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}