package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class NoRender extends Module {

    public Setting<Boolean> noWeather = register(new Setting<>("Weather", true));
    public Setting<Boolean> noExplosion = register(new Setting<>("Explosions", true));
    public Setting<Boolean> noHurtCam = register(new Setting<>("HurtCam", true));
    public Setting<Boolean> noPortal = register(new Setting<>("Portal", true));
    public Setting<Boolean> noFireOverlay = register(new Setting<>("FireOverlay", true));
    public Setting<Boolean> noBlindness = register(new Setting<>("Blindness", true));
    public Setting<Boolean> noFov = register(new Setting<>("FovEffects", true));
    public Setting<Boolean> reduceParticles = register(new Setting<>("ReduceParticles", true));
    public Setting<Boolean> entityCulling = register(new Setting<>("EntityCulling", true));

    public NoRender() {
        super("NoRender", Category.RENDER, Keyboard.KEY_NONE);
    }
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (noWeather.getValue()) {
            mc.theWorld.setRainStrength(0.0f);
        }
    }


    @SubscribeEvent
    public void onHurtCam(EntityViewRenderEvent.CameraSetup event) {
        if (noHurtCam.getValue()) {
            event.roll = 0;
        }
    }

    @SubscribeEvent
    public void onFovModify(EntityViewRenderEvent.FOVModifier event) {
        if (noFov.getValue()) {
            event.setFOV(mc.gameSettings.fovSetting);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null) return;
        if (noBlindness.getValue()) {
            mc.thePlayer.removePotionEffect(net.minecraft.potion.Potion.blindness.id);
            mc.thePlayer.removePotionEffect(net.minecraft.potion.Potion.confusion.id);
        }

        if (noPortal.getValue()) {
            mc.thePlayer.timeInPortal = 0;
            mc.thePlayer.prevTimeInPortal = 0;
        }

        if(noExplosion.getValue()) {
        }
    }

    @SubscribeEvent
    public void onRenderWorldEntities(RenderWorldLastEvent event) {

        if (!entityCulling.getValue()) return;
        if (mc.thePlayer == null) return;

        for (Object obj : mc.theWorld.loadedEntityList) {

            if (!(obj instanceof Entity)) continue;

            Entity entity = (Entity) obj;

            if (entity == mc.thePlayer) continue;

            double distance = mc.thePlayer.getDistanceToEntity(entity);

            if (distance > 80) {
                entity.setInvisible(true);
            } else {
                entity.setInvisible(false);
            }
        }
    }
}