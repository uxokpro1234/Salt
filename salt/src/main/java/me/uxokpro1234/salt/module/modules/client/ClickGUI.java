package me.uxokpro1234.salt.module.modules.client;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", Category.CLIENT, Keyboard.KEY_U);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Salt.INSTANCE.clickGUI);
        toggle();
    }
}