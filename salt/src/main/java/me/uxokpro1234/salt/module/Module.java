package me.uxokpro1234.salt.module;

import me.uxokpro1234.salt.event.events.PacketEvent;
import me.uxokpro1234.salt.event.events.PlayerMoveEvent;
import me.uxokpro1234.salt.event.events.ReachEvent;
import me.uxokpro1234.salt.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private final String name;
    private final Category category;
    private boolean enabled = false;

    public static Minecraft mc = Minecraft.getMinecraft();
    private final List<Setting<?>> settings = new ArrayList<>();
    public static List<Module> modules = new ArrayList<>();

    private Bind bind;

    public Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        this.bind = new Bind(defaultKey);
        modules.add(this);
    }

    protected <T> Setting<T> register(Setting<T> setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Bind getBind() {
        return bind;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) enable();
        else disable();
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;

        if (enabled) {
            enable();
        } else {
            disable();
        }
    }
    public void onWorldJoin(WorldEvent.Load event){ }
    public void onReceive(PacketEvent.Receive event){ }
    public void onRenderWorld(RenderWorldLastEvent event){ }
    public void onEnable() { }

    public void enable() {
        MinecraftForge.EVENT_BUS.register(this);
        onEnable();
    }

    public void onReach(ReachEvent event) {}

        public void onTick() { }

    public void onUpdate() { }

    public void onDisable() { }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        onDisable();
    }
    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) { }
    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) { }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) { }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
                module.onUpdate();
            }
        }
    }
    public enum Category {COMBAT, MOVEMENT, PLAYER, RENDER, MISC, CLIENT}
}