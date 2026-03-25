package com.example.novaclient.module.modules.render;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.RenderHudEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {
    public HUD() {
        super("HUD", "Displays module list", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
        setEnabled(true);
    }
    
    @EventHandler
    public void onRenderHud(RenderHudEvent event) {
        if (mc.player == null) return;
        
        int y = 2;
        
        event.getContext().drawText(
            mc.textRenderer,
            "§b§lNova Client §r§7v2.0",
            2,
            y,
            0xFFFFFF,
            true
        );
        y += 12;
        
        List<Module> enabled = new ArrayList<>();
        for (Module module : NovaClient.getInstance().getModuleManager().getModules()) {
            if (module.isEnabled() && module != this) {
                enabled.add(module);
            }
        }
        
        enabled.sort(Comparator.comparing(m -> -mc.textRenderer.getWidth(m.getName())));
        
        for (Module module : enabled) {
            String text = module.getName();
            int color = module.getCategory().getColor();
            
            event.getContext().drawText(
                mc.textRenderer,
                text,
                2,
                y,
                color,
                true
            );
            
            y += 10;
        }
    }
}