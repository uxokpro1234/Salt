package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.modules.render.PlayerShader;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayerChams {

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("HEAD"))
    private void preRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        PlayerShader module = Salt.INSTANCE.moduleManager.getModuleByClass(PlayerShader.class);
        if (module == null || !module.isEnabled()) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);

        float scale = 1.0f;
        if (module.pulsing.getValue()) {
            scale += module.pulseAmount.getValue() * (float)Math.sin(System.currentTimeMillis() * module.pulseSpeed.getValue() * 100);
        }
        GlStateManager.scale(scale, scale, scale);

        float r = module.r.getValue() / 255f;
        float g = module.g.getValue() / 255f;
        float b = module.b.getValue() / 255f;
        float a = module.a.getValue() / 255f;

        if (module.rainbow.getValue()) {
            float t = (System.currentTimeMillis() % 10000L) / 10000f;
            r = (float)Math.sin(t * Math.PI * 2) * 0.5f + 0.5f;
            g = (float)Math.sin(t * Math.PI * 2 + 2) * 0.5f + 0.5f;
            b = (float)Math.sin(t * Math.PI * 2 + 4) * 0.5f + 0.5f;
        }

        GL11.glColor4f(r, g, b, a);

        if (module.throughWalls.getValue()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        if (module.wireframe.getValue()) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glLineWidth(module.lineWidth.getValue());
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("RETURN"))
    private void postRender(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        PlayerShader module = Salt.INSTANCE.moduleManager.getModuleByClass(PlayerShader.class);

        if (module == null || !module.isEnabled()) return;

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.popMatrix();
    }
}