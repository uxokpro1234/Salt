package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {

    public Setting<Boolean> noRotate = register(new Setting<>("NoRotate", true));
    private float savedYaw;
    private float savedPitch;
    private boolean shouldRestore;

    public NoRotate() {
        super("NoRotate", Category.PLAYER, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {

        if (!noRotate.getValue()) return;
        if (!(event.getPacket() instanceof S08PacketPlayerPosLook)) return;
        if (mc.thePlayer == null) return;

        savedYaw = mc.thePlayer.rotationYaw;
        savedPitch = mc.thePlayer.rotationPitch;
        shouldRestore = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!shouldRestore) return;
        if (mc.thePlayer == null) return;

        mc.thePlayer.rotationYaw = savedYaw;
        mc.thePlayer.rotationPitch = savedPitch;
        shouldRestore = false;
    }
}