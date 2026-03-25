package com.example.novaclient.module.modules.render;

import com.example.novaclient.NovaClient;
import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.RenderWorldEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class ESP extends Module {
    private boolean players = true;
    private boolean mobs = false;
    private boolean items = false;
    
    public ESP() {
        super("ESP", "Highlights entities", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
    
    @EventHandler
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;
        
        RenderUtil.setupRender();
        
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;
            
            float red = 1.0f;
            float green = 1.0f;
            float blue = 1.0f;
            
            if (entity instanceof PlayerEntity) {
                if (NovaClient.getInstance().getFriendManager().isFriend(entity.getName().getString())) {
                    red = 0.0f;
                    green = 1.0f;
                    blue = 1.0f;
                } else {
                    red = 1.0f;
                    green = 0.0f;
                    blue = 0.0f;
                }
            }
            
            RenderUtil.drawEntityBox(event.getMatrices(), entity, red, green, blue, 1.0f);
        }
        
        RenderUtil.endRender();
    }
    
    private boolean shouldRender(Entity entity) {
        if (entity instanceof PlayerEntity) return players;
        return false;
    }
}