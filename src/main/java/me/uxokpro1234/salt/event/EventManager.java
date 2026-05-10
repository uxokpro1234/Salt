package me.uxokpro1234.salt.event;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class EventManager {

    public EventManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent event) {
        int key = org.lwjgl.input.Keyboard.getEventKey();
        boolean pressed = org.lwjgl.input.Keyboard.getEventKeyState();

        if (key <= 0 || !pressed) return;

        for (Module module : Salt.INSTANCE.moduleManager.getModules()) {
            if (module.getBind() != null && module.getBind().getKey() == key) {
                module.toggle();
            }
        }
    }
}