package me.uxokpro1234.salt.gui.comps;

import me.uxokpro1234.salt.gui.api.AbstractContainer;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ModuleComponent extends AbstractContainer {

    private final Module module;
    private boolean hovered;
    private boolean expanded;

    private static final int SETTING_HEIGHT = 18;

    private Setting<?> draggingSetting = null;
    private boolean bindingKey = false;

    private static final Color ENABLED_COLOR = new Color(152, 0, 13, 255);
    private static final Color BG_COLOR = new Color(25, 25, 25, 220);
    private static final Color HOVER_COLOR = new Color(35, 35, 35, 230);
    private static final Color SETTING_COLOR = new Color(30, 30, 30, 220);
    private static final Color SETTING_HOVER = new Color(40, 40, 40, 230);

    public ModuleComponent(Module module, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        hovered = isInside(mouseX, mouseY);

        int bg = module.isEnabled()
                ? ENABLED_COLOR.getRGB()
                : hovered ? HOVER_COLOR.getRGB() : BG_COLOR.getRGB();

        Gui.drawRect(x, y, x + width, y + height, bg);

        if (module.isEnabled()) {
            Gui.drawRect(x, y, x + 2, y + height, Color.WHITE.getRGB());
        }

        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(module.getName(), x + 6, y + (height / 2f - 4), 0xFFFFFF);

        if (!expanded) return;
        int yOffset = y + height;
        for (Setting<?> setting : module.getSettings()) {
            drawSetting(setting, mouseX, mouseY, yOffset);
            yOffset += SETTING_HEIGHT;
        }

        drawBindField(mouseX, mouseY, yOffset);

        if (draggingSetting != null) {
            updateNumericSetting(draggingSetting, mouseX);
        }
    }

    private void drawSetting(Setting<?> setting, int mouseX, int mouseY, int y) {
        boolean hoveredSetting = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + SETTING_HEIGHT;

        Gui.drawRect(x, y, x + width, y + SETTING_HEIGHT, hoveredSetting ? SETTING_HOVER.getRGB() : SETTING_COLOR.getRGB());

        String valueText = setting.isEnum() ? ((Enum<?>) setting.getValue()).name() : setting.getValue().toString();

        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(setting.getName() + ": " + valueText, x + 6, y + 4, 0xCCCCCC
        );

        if (setting.isNumber()) {
            double percent = getSettingPercent(setting);
            int sliderWidth = (int) (percent * width);

            Gui.drawRect(x, y + SETTING_HEIGHT - 2, x + sliderWidth, y + SETTING_HEIGHT - 1, ENABLED_COLOR.getRGB());
        }
    }

    private void drawBindField(int mouseX, int mouseY, int y) {
        boolean hoveredBind = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + SETTING_HEIGHT;

        Gui.drawRect(x, y, x + width, y + SETTING_HEIGHT, hoveredBind ? SETTING_HOVER.getRGB() : SETTING_COLOR.getRGB());
        String text = bindingKey ? "Keybind: ..." : "Keybind: " + Keyboard.getKeyName(module.getBind().getKey());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x + 6, y + 4, 0xAAAAAA);
    }

    private double getSettingPercent(Setting<?> setting) {
        double val = ((Number) setting.getValue()).doubleValue();
        double min = ((Number) setting.getMin()).doubleValue();
        double max = ((Number) setting.getMax()).doubleValue();
        return (val - min) / (max - min);
    }

    @SuppressWarnings("unchecked")
    private void updateNumericSetting(Setting<?> setting, int mouseX) {
        double min = ((Number) setting.getMin()).doubleValue();
        double max = ((Number) setting.getMax()).doubleValue();

        double percent = (mouseX - x) / (double) width;
        percent = Math.max(0, Math.min(1, percent));

        double value = min + percent * (max - min);

        if (setting.getValue() instanceof Integer) {
            ((Setting<Integer>) setting).setValue((int) value);
        } else if (setting.getValue() instanceof Float) {
            ((Setting<Float>) setting).setValue((float) value);
        } else if (setting.getValue() instanceof Double) {
            ((Setting<Double>) setting).setValue(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered) {
            if (mouseButton == 0) module.toggle();
            if (mouseButton == 1) expanded = !expanded;
        }

        if (!expanded) return;

        int yOffset = y + height;

        for (Setting<?> setting : module.getSettings()) {
            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= yOffset && mouseY <= yOffset + SETTING_HEIGHT) {

                if (setting.isBoolean() && mouseButton == 0) {
                    Setting<Boolean> bool = (Setting<Boolean>) setting;
                    bool.setValue(!bool.getValue());
                }

                else if (setting.isNumber() && mouseButton == 0) {
                    draggingSetting = setting;
                }

                else if (setting.isEnum()) {
                    if (mouseButton == 0) setting.cycleEnum();
                    if (mouseButton == 1) setting.cycleEnumBackwards();
                }
            }
            yOffset += SETTING_HEIGHT;
        }

        if (mouseX >= x && mouseX <= x + width &&
                mouseY >= yOffset && mouseY <= yOffset + SETTING_HEIGHT) {
            if (mouseButton == 0) bindingKey = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        draggingSetting = null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!bindingKey) return;

        module.getBind().setKey(
                keyCode == Keyboard.KEY_ESCAPE ? 0 : keyCode
        );

        bindingKey = false;
    }

    @Override
    public int getHeight() {
        if (!expanded) return height;
        return height + module.getSettings().size() * SETTING_HEIGHT + SETTING_HEIGHT;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }
}