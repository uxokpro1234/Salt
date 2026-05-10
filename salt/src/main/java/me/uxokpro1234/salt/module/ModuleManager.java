package me.uxokpro1234.salt.module;

import me.uxokpro1234.salt.module.modules.client.ArrayListHUD;
import me.uxokpro1234.salt.module.modules.client.ClickGUI;
import me.uxokpro1234.salt.module.modules.client.HUD;
import me.uxokpro1234.salt.module.modules.misc.InventoryMove;
import me.uxokpro1234.salt.module.modules.combat.AimAssist;
import me.uxokpro1234.salt.module.modules.combat.SpinBot;
import me.uxokpro1234.salt.module.modules.combat.TriggerBot;
import me.uxokpro1234.salt.module.modules.combat.Velocity;
import me.uxokpro1234.salt.module.modules.movement.*;
import me.uxokpro1234.salt.module.modules.player.*;
import me.uxokpro1234.salt.module.modules.render.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules;


    public ModuleManager() {
        modules = new ArrayList<Module>();
        modules.add(new ClickGUI());
        modules.add(new HUD());
        modules.add(new Sprint());
        modules.add(new FullBright());
        modules.add(new BedRender());
        modules.add(new Tracers());
        modules.add(new TriggerBot());
        modules.add(new BedBreaker());
        modules.add(new ArrayListHUD());
        modules.add(new PlayerESP());
        modules.add(new NameTags());
        modules.add(new NoHurtCam());
        modules.add(new ViewModel());
        modules.add(new SafeWalk());
        modules.add(new PlayerShader());
        modules.add(new Speed());
        modules.add(new Velocity());
        modules.add(new NoSlow());
        modules.add(new AntiHunger());
        modules.add(new NoRotate());
        modules.add(new Reach());
        modules.add(new BoatFly());
        modules.add(new MinecartFly());
        modules.add(new PacketMine());
        modules.add(new XRay());
        modules.add(new SpinBot());
        modules.add(new Op2525());
        modules.add(new AimAssist());
        modules.add(new InventoryMove());
        modules.add(new NoRender());
        modules.add(new NoClip());
        modules.add(new PacketFly());
    }

    public void onUpdate() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onUpdate();
            }
        }
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    // Get module by class
    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : modules) {
            if (clazz.isInstance(module)) return (T) module;
        }
        return null;
    }

    public void enableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) module.enable();
    }

    public void disableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) module.disable();
    }

    public void enableModule(Class<? extends Module> clazz) {
        Module module = getModuleByClass(clazz);
        if (module != null) module.enable();
    }

    public void disableModule(Class<? extends Module> clazz) {
        Module module = getModuleByClass(clazz);
        if (module != null) module.disable();
    }

    public boolean isModuleEnabled(String name) {
        Module module = getModuleByName(name);
        return module != null && module.isEnabled();
    }

    public boolean isModuleEnabled(Class<? extends Module> clazz) {
        Module module = getModuleByClass(clazz);
        return module != null && module.isEnabled();
    }

    public List<Module> getModulesInCategory(Module.Category category) {
        List<Module> categoryModules = new LinkedList<Module>();
        for (Module module : getModules()) {
            if (module.getCategory().equals(category)) categoryModules.add(module);
        }
        return categoryModules;
    }
}