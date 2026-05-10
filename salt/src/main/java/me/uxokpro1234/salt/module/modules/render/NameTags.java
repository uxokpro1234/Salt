package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {

    public Setting<Float> scale = register(new Setting<>("Scale", 1.0f, 0.5f, 3.0f));
    public Setting<Boolean> health = register(new Setting<>("Health", true));
    public Setting<Boolean> background = register(new Setting<>("Background", true));
    public Setting<Boolean> sneakFade = register(new Setting<>("SneakFade", true));

    private static final NameTags INSTANCE = new NameTags();
    private final Minecraft mc = Minecraft.getMinecraft();

    public static NameTags getInstance() {
        return INSTANCE;
    }

    public NameTags() {
        super("NameTags", Category.RENDER, Keyboard.CHAR_NONE);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void renderCustomNameTag(AbstractClientPlayer player, double x, double y, double z) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y + player.height + 0.5F, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);

        float scale = 0.025F;
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        String name = player.getName();

        int width = mc.fontRendererObj.getStringWidth(name) / 2;
        drawBox(-width - 2, -2, width + 2, 9);
        mc.fontRendererObj.drawString(name, -width, 0, 0xFFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawBox(int x1, int y1, int x2, int y2) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        GL11.glLineWidth(2.0F);

        wr.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        wr.pos(x1, y1, 0).endVertex();
        wr.pos(x2, y1, 0).endVertex();
        wr.pos(x2, y2, 0).endVertex();
        wr.pos(x1, y2, 0).endVertex();
        tess.draw();
    }
}