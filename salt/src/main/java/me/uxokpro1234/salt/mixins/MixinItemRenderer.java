package me.uxokpro1234.salt.mixins;


import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.modules.render.ViewModel;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    // Our independent smooth timer
    private float smoothSwingTimer = 0f;

    @Inject(method = "renderItemInFirstPerson(F)V",
            at = @At(value = "HEAD"))
    private void applyViewModel(float partialTicks, CallbackInfo ci) {
        ViewModel vm = Salt.INSTANCE.moduleManager.getModuleByClass(ViewModel.class);
        if (vm == null || !vm.isEnabled()) return;

        smoothSwingTimer += 0.05f * vm.swingSpeed.getValue();
        float swingX = 0f, swingY = 0f, swingZ = 0f;
        float rotX = 0f, rotY = 0f, rotZ = 0f;

        switch (vm.swingMode.getValue()) {
            case SMOOTH:
                swingX = (float) Math.sin(smoothSwingTimer) * vm.swingAmplitude.getValue();
                swingY = (float) Math.cos(smoothSwingTimer * 2) * vm.swingAmplitude.getValue();
                swingZ = (float) Math.sin(smoothSwingTimer * 0.5) * vm.swingAmplitude.getValue();
                rotX = swingX * 20f;
                rotY = swingY * 20f;
                rotZ = swingZ * 20f;
                break;

            case HEAVY:
                swingX = (float) Math.sin(smoothSwingTimer * 2) * vm.swingAmplitude.getValue() * 1.5f;
                swingY = (float) Math.sin(smoothSwingTimer * 4) * vm.swingAmplitude.getValue() * 1.5f;
                swingZ = (float) Math.sin(smoothSwingTimer) * vm.swingAmplitude.getValue() * 2f;
                rotX = swingX * 40f;
                rotY = swingY * 40f;
                rotZ = swingZ * 40f;
                break;

            case WAVE:
                swingX = (float) Math.sin(smoothSwingTimer) * vm.swingAmplitude.getValue();
                swingY = (float) Math.sin(smoothSwingTimer * 0.5) * vm.swingAmplitude.getValue();
                swingZ = (float) Math.cos(smoothSwingTimer * 1.5) * vm.swingAmplitude.getValue();
                rotX = swingX * 30f;
                rotY = swingY * 30f;
                rotZ = swingZ * 30f;
                break;

            case SLOW:
                float slowSwing = (float) Math.sin(smoothSwingTimer * 0.3) * vm.swingAmplitude.getValue();
                swingX = slowSwing;
                swingY = 0f;
                swingZ = 0f;
                rotX = slowSwing * 20f;
                rotY = 0f;
                rotZ = 0f;
                break;

        }

        GlStateManager.translate(vm.x.getValue() + swingX,
                vm.y.getValue() + swingY,
                vm.z.getValue() + swingZ);

        GlStateManager.rotate(vm.rotationX.getValue() + rotX, 1f, 0f, 0f);
        GlStateManager.rotate(vm.rotationY.getValue() + rotY, 0f, 1f, 0f);
        GlStateManager.rotate(vm.rotationZ.getValue() + rotZ, 0f, 0f, 1f);

        GlStateManager.scale(vm.scale.getValue(), vm.scale.getValue(), vm.scale.getValue());
    }
}