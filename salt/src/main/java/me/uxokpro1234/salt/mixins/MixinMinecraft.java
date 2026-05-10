package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.Salt;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "startGame", at = @At("HEAD"))
    private void startGame(CallbackInfo ci) {
        System.out.println("Mixin worked!");
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        if (Salt.INSTANCE != null && Salt.INSTANCE.configManager != null) {
            Salt.INSTANCE.configManager.save("default");
        }
    }
}