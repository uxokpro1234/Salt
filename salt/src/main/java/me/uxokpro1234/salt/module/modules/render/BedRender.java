package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import me.uxokpro1234.salt.util.RenderUtil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BedRender extends Module {
    public Setting<Integer> r = register(new Setting<>("Red", 100, 1, 255));
    public Setting<Integer> g = register(new Setting<>("Green", 100, 1, 255));
    public Setting<Integer> b = register(new Setting<>("Blue", 100, 1, 255));
    public Setting<Integer> a = register(new Setting<>("Alpha", 100, 1, 255));
    public Setting<Integer> range = register(new Setting<>("Range", 20, 1, 50));

    public Setting<Boolean> tr = register(new Setting<>("drawOutlinedBoundingBox", false));

    public BedRender() {
        super("BedRender", Category.RENDER, Keyboard.CHAR_NONE);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.theWorld == null || mc.thePlayer == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        int rRange = range.getValue();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Color color = new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue());

        for (int x = (int) mc.thePlayer.posX - rRange; x <= mc.thePlayer.posX + rRange; x++) {
            for (int y = (int) mc.thePlayer.posY - 5; y <= mc.thePlayer.posY + 5; y++) {
                for (int z = (int) mc.thePlayer.posZ - rRange; z <= mc.thePlayer.posZ + rRange; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = mc.theWorld.getBlockState(pos);

                    if (state.getBlock() instanceof BlockBed) {
                        AxisAlignedBB bb = state.getBlock().getSelectedBoundingBox(mc.theWorld, pos).offset(-camX, -camY, -camZ);
                        RenderUtil.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    }
                }
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}