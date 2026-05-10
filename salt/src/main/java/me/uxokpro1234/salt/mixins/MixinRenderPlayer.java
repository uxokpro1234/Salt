package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.module.modules.render.NameTags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {

    @Inject(method = "renderOffsetLivingLabel", at = @At("HEAD"), cancellable = true)
    private void onRenderOffsetLivingLabel(
            AbstractClientPlayer player,
            double x,
            double y,
            double z,
            String name,
            float scale,
            double distance,
            CallbackInfo ci
    ) {
        NameTags tags = NameTags.getInstance();
        if (tags == null || !tags.isEnabled()) return;

        tags.renderCustomNameTag(player, x, y, z);
        ci.cancel();
    }
}