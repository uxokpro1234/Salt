package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import me.uxokpro1234.salt.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class XRay extends Module {

    public Setting<Integer> scanRange = register(new Setting<>("ChunkRange", 4, 1, 8));
    private final List<BlockPos> foundOres = new ArrayList<>();

    public XRay() {
        super("XRay", Category.RENDER, Keyboard.CHAR_NONE);
    }

    @Override
    public void onEnable() {
        foundOres.clear();
        scanNearbyChunks();
    }

    @Override
    public void onDisable() {
        foundOres.clear();
    }

    @SubscribeEvent
    public void onWorldJoin(WorldEvent.Load event) {
        foundOres.clear();
        scanNearbyChunks();
    }
    private void scanNearbyChunks() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        int playerChunkX = (int) mc.thePlayer.posX >> 4;
        int playerChunkZ = (int) mc.thePlayer.posZ >> 4;
        int range = scanRange.getValue();

        for (int cx = playerChunkX - range; cx <= playerChunkX + range; cx++) {
            for (int cz = playerChunkZ - range; cz <= playerChunkZ + range; cz++) {
                Chunk chunk = mc.theWorld.getChunkFromChunkCoords(cx, cz);
                if (chunk != null && !chunk.isEmpty()) {
                    scanChunk(chunk);
                }
            }
        }
    }

    private void scanChunk(Chunk chunk) {
        int baseX = chunk.xPosition << 4;
        int baseZ = chunk.zPosition << 4;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(baseX + x, y, baseZ + z)).getBlock();
                    if (isOre(block)) {
                        foundOres.add(new BlockPos(baseX + x, y, baseZ + z));
                    }
                }
            }
        }
    }

    private boolean isOre(Block block) {
        return block == Blocks.diamond_ore ||
                block == Blocks.gold_ore ||
                block == Blocks.iron_ore ||
                block == Blocks.emerald_ore;
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!isEnabled()) return;
        Chunk chunk = event.getChunk();
        if (chunk != null && !chunk.isEmpty()) scanChunk(chunk);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.thePlayer == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        List<BlockPos> oresToRender = new ArrayList<>(foundOres);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glLineWidth(1.5f);

        for (BlockPos pos : oresToRender) {
            AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, pos.getX() + 1 - camX, pos.getY() + 1 - camY, pos.getZ() + 1 - camZ
            );
            RenderUtil.drawOutlinedBoundingBox(bb, 0, 255, 255, 120);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}