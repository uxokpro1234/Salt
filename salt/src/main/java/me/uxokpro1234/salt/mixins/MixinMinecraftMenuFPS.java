package me.uxokpro1234.salt.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftMenuFPS {

    /**
     * @author Dragon6555
     * @reason Increase FPS limit in menus, loading screens, and server browser
     */
    @Overwrite
    public int getLimitFramerate() {
        Minecraft mc = (Minecraft) (Object) this;

        if (mc.currentScreen != null || mc.theWorld == null) {
            return 300;
        }

        return mc.gameSettings.limitFramerate;
    }
}