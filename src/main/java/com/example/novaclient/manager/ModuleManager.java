package com.example.novaclient.manager;

import com.example.novaclient.module.Module;
import com.example.novaclient.module.modules.combat.*;
import com.example.novaclient.module.modules.misc.*;
import com.example.novaclient.module.modules.movement.*;
import com.example.novaclient.module.modules.render.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new AimAssist());
        modules.add(new AnchorAura());
        modules.add(new AntiBot());
        modules.add(new AutoCart());
        modules.add(new AutoCrystal());
        modules.add(new AutoDoubleHand());
        modules.add(new AutoInventoryTotem());
        modules.add(new AutoMace());
        modules.add(new AutoTotem());
        modules.add(new BedAura());
        modules.add(new BreachSwap());
        modules.add(new BurrowBot());
        modules.add(new CevBreaker());
        modules.add(new CrystalAura());
        modules.add(new ElytraHotSwap());
        modules.add(new Hitboxes());
        modules.add(new HoleFiller());
        modules.add(new KeyAnchor());
        modules.add(new KillAura());
        modules.add(new MacePVP());
        modules.add(new PopCounter());
        modules.add(new ShieldBreaker());
        modules.add(new Surround());
        modules.add(new SurroundBreaker());
        modules.add(new TriggerBot());

        modules.add(new AntiCrash());
        modules.add(new AutoEXP());
        modules.add(new NoBreakDelay());
        modules.add(new Teams());

        modules.add(new Flight());
        modules.add(new Speed());
        modules.add(new Velocity());

        modules.add(new CrystalESP());
        modules.add(new ESP());
        modules.add(new Fullbright());
        modules.add(new HUD());
        modules.add(new HoleESP());
        modules.add(new NameTags());
        modules.add(new NoBounce());
        modules.add(new NoRender());
        modules.add(new Tracers());
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) return module;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) return (T) module;
        }
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(com.example.novaclient.module.Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) result.add(module);
        }
        return result;
    }
}