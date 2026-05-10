package me.uxokpro1234.salt.module.modules.client;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayListHUD extends Module {

    public enum ColorMode {
        STATIC,
        RAINBOW,
        GRADIENT,
        CATEGORY
    }

    public enum AnimMode {
        POP,
        BOUNCE,
        WOBBLE,
        WATER,
        SLIDE
    }

    public Setting<ColorMode> colorMode = register(new Setting<>("ColorMode", ColorMode.RAINBOW));
    public Setting<AnimMode> animMode = register(new Setting<>("AnimMode", AnimMode.POP));
    public Setting<Boolean> background = register(new Setting<>("Background", true));
    public Setting<Boolean> perLetterRainbow = register(new Setting<>("PerLetter", true));
    public Setting<Boolean> waterEffect = register(new Setting<>("WaterEffect", true));
    public Setting<Float> letterScale = register(new Setting<>("LetterScale", 1.0f, 0.5f, 2.0f));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 255, 0, 255));
    public Setting<Integer> bgAlpha = register(new Setting<>("BGAlpha", 120, 0, 255));
    public Setting<Integer> yOffset = register(new Setting<>("YOffset", 6, 0, 50));
    public Setting<Float> animSpeed = register(new Setting<>("AnimSpeed", 0.2f, 0.05f, 1f));

    private final Map<Module, Float> animationMap = new HashMap<>();

    public ArrayListHUD() {
        super("ArrayList", Category.CLIENT, Keyboard.CHAR_NONE);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        List<Module> modules = Salt.INSTANCE.moduleManager.getModules().stream().filter(m -> m != this).sorted(Comparator.comparingInt(m -> -fr.getStringWidth(m.getName()))).collect(Collectors.toList());

        int y = yOffset.getValue();
        int index = 0;

        for (Module m : modules) {

            animationMap.putIfAbsent(m, 0f);
            float anim = animationMap.get(m);
            float target = m.isEnabled() ? 1f : 0f;
            anim += (target - anim) * animSpeed.getValue();
            animationMap.put(m, anim);

            if (anim < 0.01f) continue;

            String text = m.getName();
            float scale = letterScale.getValue();
            double wave = 0;
            int textWidth = fr.getStringWidth(text);
            int x = sr.getScaledWidth() - textWidth - 6;

            switch (animMode.getValue()) {
                case POP:
                    scale = letterScale.getValue() * (0.8f + 0.2f * (float) Math.sin(anim * Math.PI));
                    break;
                case BOUNCE:
                    scale = letterScale.getValue() * (0.7f + 0.3f * (float) Math.sin(anim * Math.PI * 2));
                    break;
                case WOBBLE:
                    scale = letterScale.getValue() * (0.85f + 0.15f * (float) Math.sin(anim * Math.PI * 3));
                    if (waterEffect.getValue())
                        wave = Math.sin(System.currentTimeMillis() / 100.0 + index) * 2.5;
                    break;
                case WATER:
                    scale = letterScale.getValue() * (0.8f + 0.2f * (float) Math.sin(anim * Math.PI));
                    if (waterEffect.getValue())
                        wave = Math.sin(System.currentTimeMillis() / 150.0 + index) * 3.0;
                    break;
                case SLIDE:
                    scale = letterScale.getValue();
                    x = (int) (sr.getScaledWidth() - textWidth - 6 + (1 - anim) * textWidth);
                    break;
            }

            int scaledWidth = (int) (textWidth * scale);
            GL11.glPushMatrix();
            GL11.glTranslated(x + scaledWidth / 2.0, y + fr.FONT_HEIGHT / 2.0 + wave, 0);
            GL11.glScalef(scale, scale, 1f);
            GL11.glTranslated(-(x + scaledWidth / 2.0), -(y + fr.FONT_HEIGHT / 2.0 + wave), 0);

            if (background.getValue()) {
                drawRect(x - 2, y - 1, x + textWidth + 2, y + fr.FONT_HEIGHT + 1, new Color(0, 0, 0, bgAlpha.getValue()).getRGB()
                );
            }

            if (perLetterRainbow.getValue() && colorMode.getValue() == ColorMode.RAINBOW) {
                drawRainbowString(fr, text, x, y, index);
            } else {
                fr.drawStringWithShadow(text, x, y, getModuleColor(m, index).getRGB());
            }

            GL11.glPopMatrix();

            y += fr.FONT_HEIGHT + 2;
            index++;
        }
    }

    private Color getModuleColor(Module m, int index) {
        switch (colorMode.getValue()) {
            case STATIC:
                return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
            case RAINBOW:
                return getRainbow(index * 300);
            case GRADIENT:
                float f = Math.min(1f, index / 10f);
                return new Color((int) (255 * (1 - f)), 80, (int) (255 * f), alpha.getValue());
            case CATEGORY:
                return getCategoryColor(m.getCategory());
        }
        return Color.WHITE;
    }

    private Color getRainbow(int offset) {
        float hue = ((System.currentTimeMillis() + offset) % 4000L) / 4000f;
        return Color.getHSBColor(hue, 0.9f, 1f);
    }

    private void drawRainbowString(FontRenderer fr, String text, int x, int y, int seed) {
        int off = 0;
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            Color col = getRainbow(i * 180 + seed * 300);
            fr.drawStringWithShadow(c, x + off, y, col.getRGB());
            off += fr.getStringWidth(c);
        }
    }

    private Color getCategoryColor(Category c) {
        switch (c) {
            case COMBAT:
                return new Color(255, 80, 80);
            case MOVEMENT:
                return new Color(80, 160, 255);
            case RENDER:
                return new Color(180, 80, 255);
            case CLIENT:
                return new Color(255, 255, 120);
        }
        return Color.WHITE;
    }

    private void drawRect(int l, int t, int r, int b, int c) {
        net.minecraft.client.gui.Gui.drawRect(l, t, r, b, c);
    }
}