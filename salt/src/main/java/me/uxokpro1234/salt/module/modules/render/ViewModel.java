package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import org.lwjgl.input.Keyboard;

public class ViewModel extends Module {

    public Setting<Float> x = register(new Setting<>("X", 1.36f, -2.0f, 2.0f));
    public Setting<Float> y = register(new Setting<>("Y", -1.8f, -2.0f, 2.0f));
    public Setting<Float> z = register(new Setting<>("Z", -1.6f, -2.0f, 2.0f));
    public Setting<Float> scale = register(new Setting<>("Scale", 1.325f, 0.1f, 5.0f));
    public Setting<Float> rotationX = register(new Setting<>("RotX", 3.6f, -180.0f, 180.0f));
    public Setting<Float> rotationY = register(new Setting<>("RotY", -10.8f, -180.0f, 180.0f));
    public Setting<Float> rotationZ = register(new Setting<>("RotZ", 93.6f, -180.0f, 180.0f));
    public enum SwingMode { SMOOTH, HEAVY, WAVE, SLOW }
    public Setting<SwingMode> swingMode = register(new Setting<>("SwingMode", SwingMode.WAVE));
    public Setting<Float> swingAmplitude = register(new Setting<>("SwingAmp", 0.08f, 0.0f, 0.5f));
    public Setting<Float> swingSpeed = register(new Setting<>("SwingSpeed", 0.1f, 0.01f, 5.0f));

    public ViewModel() {
        super("ViewModel", Category.RENDER, Keyboard.CHAR_NONE);
    }
}