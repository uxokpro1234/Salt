package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import org.lwjgl.input.Keyboard;

public class PlayerShader extends Module {

    private static final PlayerShader INSTANCE = new PlayerShader();
    public Setting<Integer> r = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> g = register(new Setting<>("Green", 0, 0, 255));
    public Setting<Integer> b = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> a = register(new Setting<>("Alpha", 180, 0, 255));
    public Setting<Boolean> rainbow = register(new Setting<>("Rainbow", true));
    public Setting<Boolean> pulsing = register(new Setting<>("Pulsing", true));
    public Setting<Float> pulseSpeed = register(new Setting<>("PulseSpeed", 0.01f, 0.001f, 0.1f));
    public Setting<Float> pulseAmount = register(new Setting<>("PulseAmount", 0.2f, 0.0f, 1.0f));
    public Setting<Boolean> throughWalls = register(new Setting<>("ThroughWalls", true));
    public Setting<Boolean> wireframe = register(new Setting<>("Wireframe", true));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 2.0f, 0.1f, 10.0f));
    public Setting<Boolean> skeleton = register(new Setting<>("Skeleton", true));
    public Setting<Boolean> murder = register(new Setting<>("MurderMystery", false));
    public Setting<Integer> range = register(new Setting<>("Range", 130, 1, 255));

    public PlayerShader() {
        super("PlayerShader", Category.RENDER, Keyboard.CHAR_NONE);
    }
    public static PlayerShader getInstance() {
        return INSTANCE;
    }

}