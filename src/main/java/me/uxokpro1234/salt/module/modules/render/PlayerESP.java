package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerESP extends Module {

    private final Set<UUID> ironSwordPlayers = new HashSet<>();
    public Setting<Integer> r = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> g = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> b = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> a = register(new Setting<>("Alpha", 180, 0, 255));
    public Setting<Integer> range = register(new Setting<>("Range", 130, 1, 255));
    public Setting<Boolean> throughWalls = register(new Setting<>("ThroughWalls", true));
    public Setting<Boolean> murder = register(new Setting<>("MurderMystery", true));


    public PlayerESP() {
        super("PlayerESP", Category.RENDER, Keyboard.CHAR_NONE);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        ironSwordPlayers.clear();
    }


    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        float partialTicks = event.partialTicks;
        EntityPlayerSP localPlayer = mc.thePlayer;

        for (EntityPlayer player : mc.theWorld.playerEntities) {

            ItemStack held = player.getHeldItem();
            if (held != null && held.getItem() == Items.iron_sword) {
                ironSwordPlayers.add(player.getUniqueID());
                String name = player.getName();
            }

            if (player == localPlayer) continue;
            if (player.isInvisible() || player.isDead) continue;
            if (localPlayer.getDistanceToEntity(player) > range.getValue()) continue;

            AxisAlignedBB bb = getInterpolatedBB(player, partialTicks);
            Color color = new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue());
            drawESPBox(bb, color, throughWalls.getValue());

            if (murder.getValue() &&ironSwordPlayers.contains(player.getUniqueID())) {
                Color color2 = new Color(255, 0, 0, a.getValue()); // RED
                drawESPBox(bb, color2, throughWalls.getValue());
            }
        }
    }

    private AxisAlignedBB getInterpolatedBB(EntityPlayer player, float partialTicks) {
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
        double width = player.width / 2.0;
        double height = player.height;

        if (player.isSneaking()) height -= 0.2;

        return new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
    }

    private void drawESPBox(AxisAlignedBB bb, Color color, boolean throughWalls) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (throughWalls) GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0f);

        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        drawOutlinedBoundingBox(bb);

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        if (throughWalls) GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawOutlinedBoundingBox(AxisAlignedBB bb) {
        GL11.glBegin(GL11.GL_LINES);

        line(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.minZ);
        line(bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ);
        line(bb.maxX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.maxZ);
        line(bb.minX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.minZ);

        line(bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.minZ);
        line(bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        line(bb.maxX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ);
        line(bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.minZ);

        line(bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ);
        line(bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ);
        line(bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ);
        line(bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ);

        GL11.glEnd();
    }

    private void line(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2, z2);
    }
}