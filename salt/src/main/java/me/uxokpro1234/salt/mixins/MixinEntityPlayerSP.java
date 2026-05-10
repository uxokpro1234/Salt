package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.event.events.PlayerMoveEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {

    @Shadow
    public MovementInput movementInput;

    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void onPlayerMoveHook(CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent((EntityPlayerSP) (Object) this,
                movementInput.moveForward,
                movementInput.moveStrafe);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            movementInput.moveForward = 0;
            movementInput.moveStrafe = 0;
        } else {
            movementInput.moveForward = (float) event.getMoveForward();
            movementInput.moveStrafe = (float) event.getMoveStrafe();
        }
    }
}