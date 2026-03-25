package com.example.novaclient.module.modules.render;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.RenderWorldEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.lwjgl.glfw.GLFW;

public class CrystalESP extends Module {
    public CrystalESP() {
        super("CrystalESP", "Highlights end crystals", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
    
    @EventHandler
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;
        
        RenderUtil.setupRender();
        
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                RenderUtil.drawEntityBox(event.getMatrices(), entity, 1.0f, 0.0f, 1.0f, 1.0f);
            }
        }
        
        RenderUtil.endRender();
    }
}