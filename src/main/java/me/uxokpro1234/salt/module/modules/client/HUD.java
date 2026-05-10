package me.uxokpro1234.salt.module.modules.client;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;

public class HUD extends Module {

    public Setting<Boolean> grayText = register(new Setting<>("Gray", true));
    public Setting<Boolean> showCoords = register(new Setting<>("Coords", true));
    public Setting<Boolean> showSpeed = register(new Setting<>("Speed", true));
    public Setting<Boolean> showFPS = register(new Setting<>("FPS", true));
    public Setting<Boolean> showPing = register(new Setting<>("Ping", true));
    public Setting<Boolean> showTPS = register(new Setting<>("TPS", true));
    public Setting<Float> textScale = register(new Setting<>("TextScale", 0.7f, 0.3f, 1.0f));
    public Setting<Integer> lineSpacing = register(new Setting<>("LineSpacing", 14, 8, 24));

    private long lastFPSCheck = System.currentTimeMillis();
    private int frames = 0;
    private int fps = 0;

    private final LinkedList<Long> tickTimes = new LinkedList<>();
    private int ping = 0;
    private int lastKeepAliveID = -1;
    private long keepAliveSentTime = 0;

    public HUD() {
        super("HUD", Category.CLIENT, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof C00PacketKeepAlive) {
            keepAliveSentTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S00PacketKeepAlive) {
            ping = (int) (System.currentTimeMillis() - keepAliveSentTime);
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook) return;

        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            tickTimes.add(System.currentTimeMillis());
            if (tickTimes.size() > 20) tickTimes.removeFirst();
        }
    }

    private float getTPS() {
        if (mc.isSingleplayer()) return 20.0f;
        if (tickTimes.size() < 2) return 20.0f;
        long first = tickTimes.getFirst();
        long last = tickTimes.getLast();
        long delta = last - first;
        if (delta <= 0) return 20.0f;
        return Math.min(20.0f, (tickTimes.size() - 1) * 1000.0f / delta);
    }

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer == null) return;

        ScaledResolution sr = new ScaledResolution(mc);
        float scale = textScale.getValue();
        float scaledMargin = 5 / scale;
        int lineSpacingValue = lineSpacing.getValue();
        frames++;

        if (System.currentTimeMillis() - lastFPSCheck >= 1000) {
            fps = frames;
            frames = 0;
            lastFPSCheck = System.currentTimeMillis();
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0f);
        float scaledScreenWidth = sr.getScaledWidth() / scale;
        float scaledScreenHeight = sr.getScaledHeight() / scale;
        int yBottom = (int) (scaledScreenHeight - scaledMargin);
        int potionCount = mc.thePlayer.getActivePotionEffects().size();
        int potionHeight = potionCount * lineSpacingValue;
        int statsStartY = yBottom - potionHeight;
        int yPotion = yBottom - lineSpacingValue;

        if (!mc.thePlayer.getActivePotionEffects().isEmpty()) {
            for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
                int id = effect.getPotionID();
                Potion potion = Potion.potionTypes[id];
                String potionName = potion.getName();
                int amplifier = effect.getAmplifier() + 1;
                int duration = effect.getDuration() / 20;
                int color = potion.getLiquidColor();
                String effectStr = potionName + " " + amplifier + " (" + duration + "s)";
                drawRightAligned(effectStr, scaledScreenWidth, yPotion, scaledMargin, false, color);
                yPotion -= lineSpacingValue;
            }
        }

        int yStats = statsStartY - lineSpacingValue; // start drawing stats above potion area

        if (showSpeed.getValue()) {
            double motion = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) * 20;
            drawRightAligned("Speed: " + String.format("%.2f m/s", motion), scaledScreenWidth, yStats, scaledMargin, grayText.getValue());
            yStats -= lineSpacingValue;
        }
        if (showTPS.getValue()) {
            drawRightAligned("TPS: " + String.format("%.1f", getTPS()), scaledScreenWidth, yStats, scaledMargin, grayText.getValue());
            yStats -= lineSpacingValue;
        }
        if (showFPS.getValue()) {
            drawRightAligned("FPS: " + fps, scaledScreenWidth, yStats, scaledMargin, grayText.getValue());
            yStats -= lineSpacingValue;
        }
        if (showPing.getValue()) {
            drawRightAligned("Ping: " + ping + "ms", scaledScreenWidth, yStats, scaledMargin, grayText.getValue());
        }

        GlStateManager.popMatrix();

        if (showCoords.getValue()) {
            String coords = "XYZ: " + (int) mc.thePlayer.posX + ", " + (int) mc.thePlayer.posY + ", " + (int) mc.thePlayer.posZ;
            int x = 5;
            int y = sr.getScaledHeight() - 5 - mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow(coords, x, y, grayText.getValue() ? 0xAAAAAA : 0xFFFFFF);
        }
    }

    private void drawRightAligned(String text, float screenWidth, float y, float margin, boolean gray) {
        int color = gray ? 0xAAAAAA : 0xFFFFFF;
        int x = (int) (screenWidth - mc.fontRendererObj.getStringWidth(text) - margin);
        mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
    }

    private void drawRightAligned(String text, float screenWidth, float y, float margin, boolean gray, int color) {
        int x = (int) (screenWidth - mc.fontRendererObj.getStringWidth(text) - margin);
        mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
    }
}