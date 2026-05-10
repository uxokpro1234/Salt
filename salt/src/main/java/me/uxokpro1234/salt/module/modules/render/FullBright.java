package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {
    public Setting<Double> brightness = register(new Setting<>("Brightness", 100.0, 1.0, 100.0));
    public Setting<Boolean> tr = register(new Setting<>("Spoof", false));


    public FullBright() {
        super("FullBright", Category.RENDER, Keyboard.CHAR_NONE);
    }
    public float gamma;

    @Override
    public void onEnable() {
         gamma = mc.gameSettings.gammaSetting;
        if (mc.thePlayer != null) {
            mc.gameSettings.gammaSetting = brightness.getValue().floatValue();
        }
    }
        @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.gameSettings.gammaSetting = gamma;

        }
    }
}