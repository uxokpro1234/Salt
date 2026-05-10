package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import org.lwjgl.input.Keyboard;

public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam", Category.RENDER, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
    }
}