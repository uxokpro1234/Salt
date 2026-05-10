package me.uxokpro1234.salt.module.modules.combat;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class SpinBot extends Module {

    public Setting<Float> speed = register(new Setting<>("Speed", 20f, 1f, 100f));

    private float yawServerSide = 0f;

    public SpinBot() {
        super("SpinBot", Category.COMBAT, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null) return;

        yawServerSide += speed.getValue();
        if (yawServerSide > 360f) yawServerSide -= 360f;

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yawServerSide, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
    }

    @Override
    public void onDisable() {
        yawServerSide = 0f;
    }
}