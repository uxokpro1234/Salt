package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.event.events.ReachEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> cir) {
        ReachEvent event = new ReachEvent(null, 3.0);
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue((float) event.getReach());
    }
}