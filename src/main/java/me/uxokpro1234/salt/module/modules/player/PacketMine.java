package me.uxokpro1234.salt.module.modules.player;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class PacketMine extends Module {
    public Setting<Float> alpha = register(new Setting<>("Alpha", 0.35f, 0.05f, 1.0f));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 2.0f, 1.0f, 5.0f));
    public Setting<Boolean> render = register(new Setting<>("Render", true));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> cancel = register(new Setting<>("Cancel", false));
    public Setting<Boolean> outline = register(new Setting<>("Outline", true));
    public Setting<Boolean> ss = register(new Setting<>("Silent", false));



    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private BlockPos currentPos;
    private EnumFacing currentFacing;

    private float breakProgress = 0f;
    private float blockHardness = 0f;

    private boolean mining = false;

    public PacketMine() {
        super("PacketMine", Category.PLAYER, Keyboard.KEY_NONE);
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {

        if (!(event.getPacket() instanceof C07PacketPlayerDigging)) return;

        C07PacketPlayerDigging packet = event.getPacket();

        if(ss.getValue())switchToPickaxe();

        if (packet.getStatus() != C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) return;

        if (mc.theWorld == null || mc.thePlayer == null) return;

        currentPos = packet.getPosition();
        currentFacing = packet.getFacing();

        if (mc.theWorld.isAirBlock(currentPos)) return;

        if (mc.theWorld.isAirBlock(currentPos)) return;

        IBlockState state = mc.theWorld.getBlockState(currentPos);
        Block block = state.getBlock();
        blockHardness = block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, currentPos);

        if (blockHardness <= 0) return;

        breakProgress = 0f;
        mining = true;
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (rotate.getValue() && cancel.getValue() && event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook && currentPos != null) {
            event.setCanceled(true);
            System.out.println("CANCEL");
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!mining) return;
        if (event.phase != TickEvent.Phase.END) return;

        if (currentPos == null) {
            reset();
            return;
        }

        if (mc.theWorld.isAirBlock(currentPos)) {
            reset();
            return;
        }

        if (rotate.getValue()) {
            float[] rotations = getRotationsToBlock(currentPos);
            if (Math.abs(rotations[0] - targetYaw) > 0.1f || Math.abs(rotations[1] - targetPitch) > 0.1f) { targetYaw = rotations[0];targetPitch = rotations[1];
                mc.getNetHandler().addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook(targetYaw, targetPitch, mc.thePlayer.onGround));
                System.out.println("ROTATE");
            }
        }

        breakProgress += blockHardness;

        if (breakProgress >= 1f) { mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, currentFacing));
            reset();
            if(ss.getValue())switchToPickaxe();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!render.getValue()) return;
        if (!mining) return;
        if (currentPos == null) return;

        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;
        float progress = Math.min(breakProgress, 1f);
        float scale = 0.1f + (0.9f * progress);
        float red = 1f - progress;
        float green = progress;
        float blue = 0f;
        double x = currentPos.getX() - viewerX + 0.5;
        double y = currentPos.getY() - viewerY + 0.5;
        double z = currentPos.getZ() - viewerZ + 0.5;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha.getValue());
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(scale, scale, scale);
        AxisAlignedBB box = new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5,  0.5,  0.5);
        drawFilledBox(box);
        if(outline.getValue()) drawOutlinedBox(box, red, green, blue);

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawFilledBox(AxisAlignedBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

        tessellator.draw();
    }

    private void reset() {
        currentPos = null;
        currentFacing = null;
        breakProgress = 0f;
        blockHardness = 0f;
        mining = false;
    }
    private float[] getRotationsToBlock(BlockPos pos) {
        double x = pos.getX() + 0.5 - mc.thePlayer.posX;
        double y = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double z = pos.getZ() + 0.5 - mc.thePlayer.posZ;

        double distXZ = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(y, distXZ));
        pitch = MathHelper.clamp_float(pitch, -90f, 90f);
        yaw = ((yaw % 360f) + 360f) % 360f;
        if (yaw > 180f) yaw -= 360f;

        return new float[]{yaw, pitch};
    }

    private void drawOutlinedBox(AxisAlignedBB bb, float r, float g, float b) {
        GL11.glLineWidth(lineWidth.getValue());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        float alphaOutline = 1.0f;

        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alphaOutline).endVertex();

        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    public void switchToPickaxe() {
        if (mc.thePlayer == null) return;
        int currentSlot = mc.thePlayer.inventory.currentItem;
        int pickaxeSlot = 9;
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(pickaxeSlot));
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(currentSlot));
    }
}