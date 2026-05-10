package me.uxokpro1234.salt.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawBlockOutline(BlockPos pos, Color color, float width) {
        World world = mc.theWorld;
        if (world == null) return;

        IBlockState state = world.getBlockState(pos);
        if (state == null) return;

        Block block = state.getBlock();
        AxisAlignedBB bb = block.getSelectedBoundingBox(world, pos).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(width);

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        drawOutlinedBoundingBox(bb, r, g, b, a);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }


    public static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();

        worldRenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();

        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();

        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();

        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();

        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
    }
}