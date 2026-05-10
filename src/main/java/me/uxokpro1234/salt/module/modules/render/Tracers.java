package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class Tracers extends Module {

    public enum ColorMode {STATIC, RAINBOW, GRADIENT}

    public Setting<Integer> r = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> g = register(new Setting<>("Green", 0, 0, 255));
    public Setting<Integer> b = register(new Setting<>("Blue", 0, 0, 255));
    public Setting<Integer> a = register(new Setting<>("Alpha", 150, 0, 255));
    public Setting<Integer> offset = register(new Setting<>("Alpha", 150, 0, 255));
    public Setting<Boolean> throughWalls = register(new Setting<>("ThroughWalls", true));
    public Setting<Float> width = register(new Setting<>("Width", 0.9f, 0.85f, 1.5f));
    public Setting<ColorMode> colorMode = register(new Setting<>("ColorMode", ColorMode.GRADIENT));
    public Setting<Float> startOffset = register(new Setting<>("StartOffset", 0.0f, 0.0f, 1.0f));
    public Setting<Float> endOffset = register(new Setting<>("EndOffset", 0.5f, 0.0f, 2.0f));
    public Setting<Float> maxDistance = register(new Setting<>("MaxDistance", 100f, 10f, 200f));

    public Tracers() {
        super("Tracers", Category.RENDER, Keyboard.CHAR_NONE);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.thePlayer == null || mc.theWorld == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        Vec3 cameraPos = getCameraPosition(event.partialTicks);
        Vec3 lookVec = mc.thePlayer.getLookVec();

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        if (throughWalls.getValue()) GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(width.getValue());

        List<EntityPlayer> players = mc.theWorld.playerEntities;
        for (EntityPlayer player : players) {
            if (player == mc.thePlayer) continue;

            double distance = mc.thePlayer.getDistanceToEntity(player);
            if (distance > maxDistance.getValue()) continue;

            double px = interpolate(player.lastTickPosX, player.posX, event.partialTicks);
            double py = interpolate(player.lastTickPosY, player.posY, event.partialTicks) + player.getEyeHeight() * endOffset.getValue();
            double pz = interpolate(player.lastTickPosZ, player.posZ, event.partialTicks);
            double sx = cameraPos.xCoord + lookVec.xCoord * startOffset.getValue();
            double sy = cameraPos.yCoord + lookVec.yCoord * startOffset.getValue();
            double sz = cameraPos.zCoord + lookVec.zCoord * startOffset.getValue();
            Color color = getColor(player, distance);

            // Draw line with gradient/rainbow effect
            int segments = 20;
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int i = 0; i <= segments; i++) {
                float t = i / (float) segments;
                double x = sx + (px - sx) * t - camX;
                double y = sy + (py - sy) * t - camY;
                double z = sz + (pz - sz) * t - camZ;

                Color c = getGradientColor(color, t);
                GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
                GL11.glVertex3d(x, y, z);
            }
            GL11.glEnd();
        }

        if (throughWalls.getValue()) GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private double interpolate(double last, double current, float partialTicks) {
        return last + (current - last) * partialTicks;
    }

    private Vec3 getCameraPosition(float partialTicks) {
        double x = interpolate(mc.thePlayer.prevPosX, mc.thePlayer.posX, partialTicks);
        double y = interpolate(mc.thePlayer.prevPosY, mc.thePlayer.posY, partialTicks) + mc.thePlayer.getEyeHeight();
        double z = interpolate(mc.thePlayer.prevPosZ, mc.thePlayer.posZ, partialTicks);
        return new Vec3(x, y, z);
    }

    private Color getColor(EntityPlayer player, double distance) {
        switch (colorMode.getValue()) {
            case STATIC:
                return new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue());
            case RAINBOW:
                return getRainbowColor(offset.getValue());
            case GRADIENT:
                float factor = (float) Math.min(1.0, distance / maxDistance.getValue());
                int red = (int) (255 * (1 - factor));
                int blue = (int) (255 * factor);
                return new Color(red, 0, blue, a.getValue());
            default:
                return new Color(255, 255, 255, a.getValue());
        }
    }

    private Color getRainbowColor(long offset) {

        long time = System.currentTimeMillis() + offset;
        float hue = (float)((time % 4000L) / 4000.0);
        float brightness = 0.5f + 0.5f * (float)Math.sin(time / 200.0);
        float saturation = 0.7f + 0.3f * (float)Math.cos(time / 300.0);
        hue = Math.min(Math.max(hue, 0f), 1f);
        brightness = Math.min(Math.max(brightness, 0f), 1f);
        saturation = Math.min(Math.max(saturation, 0f), 1f);
        Color rgb = Color.getHSBColor(hue, saturation, brightness);

        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), a.getValue());
    }


    private Color getGradientColor(Color base, float t) {
        switch (colorMode.getValue()) {
            case RAINBOW:
                float hue = ((System.currentTimeMillis() % 2000L) / 2000f + t) % 1f;
                return Color.getHSBColor(hue, 1f, 1f);
            case GRADIENT:
                int r = (int) (base.getRed() * (1 - t) + base.getBlue() * t);
                int g = base.getGreen();
                int b = (int) (base.getBlue() * (1 - t) + base.getRed() * t);
                return new Color(r, g, b, base.getAlpha());
            default:
                return base;
        }
    }
}