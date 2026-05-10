package me.uxokpro1234.salt.module.modules.combat;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {

    public enum Mode {
        CANCEL, CUSTOM, REDUCE, REDUCE1, ANTIKNOCK, EXPLOSION
    }

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.CANCEL));
    public Setting<Float> horizontal = register(new Setting<>("Horizontal", 0.0f, 0f, 1f));
    public Setting<Float> vertical = register(new Setting<>("Vertical", 0.0f, 0f, 1f));
    public Setting<Boolean> packett = register(new Setting<>("Packet", false));



    public Velocity() {
        super("Velocity", Category.COMBAT, Keyboard.KEY_NONE);
    }

    @Override
    public void onDisable() {
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {

        if (!(event.getPacket() instanceof S12PacketEntityVelocity)) return;
        S12PacketEntityVelocity packet = event.getPacket();
        if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;
        event.setCanceled(true);

        int newMotionX = (int) (packet.getMotionX() * horizontal.getValue());
        int newMotionY = (int) (packet.getMotionY() * vertical.getValue());
        int newMotionZ = (int) (packet.getMotionZ() * horizontal.getValue());

        System.out.println(packet);
        S12PacketEntityVelocity customPacket = new S12PacketEntityVelocity(packet.getEntityID(), newMotionX / 8000.0, newMotionY / 8000.0, newMotionZ / 8000.0);
        System.out.println(customPacket);
        try {
            if (packett.getValue()) mc.getNetHandler().handleEntityVelocity(customPacket);
        }catch (ThreadQuickExitException e){
            System.out.println(e);
        }
    }
}