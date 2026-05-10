package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiHunger extends Module {

    public Setting<Boolean> noSprint = register(new Setting<>("NoSprint", true));
    public AntiHunger() {
        super("AntiHunger", Category.PLAYER, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {

        if (noSprint.getValue() && mc.thePlayer.isSprinting()) {
            mc.thePlayer.setSprinting(false);
        }
    }
}