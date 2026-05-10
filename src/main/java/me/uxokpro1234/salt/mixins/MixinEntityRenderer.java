package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.modules.render.NoHurtCam;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "hurtCameraEffect(F)V", at = @At("HEAD"), cancellable = true)
    private void noHurtCam(float partialTicks, CallbackInfo ci) {
        if (Salt.INSTANCE.moduleManager.getModuleByClass(NoHurtCam.class).isEnabled()) {
            ci.cancel();
        }
    }
}