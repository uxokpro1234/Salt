package me.uxokpro1234.salt;

import me.uxokpro1234.salt.event.ConfigManager;
import me.uxokpro1234.salt.event.EventManager;
import me.uxokpro1234.salt.gui.Click;
import me.uxokpro1234.salt.module.ModuleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Salt.MODID, version = Salt.VERSION)
public class Salt {

    public static final String MODID = "salt";
    public static final String VERSION = "1.0";

    @Mod.Instance(Salt.MODID)
    public static Salt INSTANCE;
    public ModuleManager moduleManager;
    public EventManager eventManager;
    public ConfigManager configManager;
    public Click clickGUI;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        configManager.load("default");
        clickGUI = new Click();
    }
}