package me.uxokpro1234.salt.gui.comps;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.gui.api.AbstractContainer;
import me.uxokpro1234.salt.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryComponent extends AbstractContainer {

    private final Module.Category category;
    private final List<ModuleComponent> moduleComponents = new ArrayList<>();

    private boolean hovered;
    private boolean dragging;
    private int dragX, dragY;

    public CategoryComponent(Module.Category category, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.category = category;

        for (Module module : Salt.INSTANCE.moduleManager.getModulesInCategory(category)) {
            moduleComponents.add(new ModuleComponent(module, x, y, width, height));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if (dragging) {
            this.x = mouseX - dragX;
            this.y = mouseY - dragY;
        }

        hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        int headerColor = hovered ? new Color(35, 35, 35, 230).getRGB() : new Color(25, 25, 25, 220).getRGB();
        int borderColor = new Color(60, 60, 60, 255).getRGB();
        Gui.drawRect(x, y, x + width, y + height, headerColor);
        Gui.drawRect(x, y + height - 1, x + width, y + height, borderColor);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(category.name(), x + 4, y + (height / 2f - 4), 0xFFFFFF);
        int offsetY = y + height;

        for (ModuleComponent component : moduleComponents) {
            component.setX(x);
            component.setY(offsetY);
            component.setWidth(width);

            component.drawScreen(mouseX, mouseY, partialTicks);
            offsetY += component.getHeight();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (mouseButton == 0 && hovered) {
            dragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }

        for (ModuleComponent component : moduleComponents) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;

        for (ModuleComponent component : moduleComponents) {
            component.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {
        for (ModuleComponent component : moduleComponents) {
            component.keyTyped(keyChar, keyCode);
        }
    }

    @Override
    public int getHeight() {
        int total = height;
        for (ModuleComponent component : moduleComponents) {
            total += component.getHeight();
        }
        return total;
    }
}