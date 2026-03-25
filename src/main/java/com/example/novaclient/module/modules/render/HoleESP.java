package com.example.novaclient.module.modules.render;

import com.example.novaclient.event.EventHandler;
import com.example.novaclient.event.events.RenderWorldEvent;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.util.BlockUtil;
import com.example.novaclient.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class HoleESP extends Module {
    private int range = 10;
    
    public HoleESP() {
        super("HoleESP", "Highlights safe holes", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
    
    @EventHandler
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;
        
        RenderUtil.setupRender();
        
        BlockPos playerPos = BlockUtil.getPlayerBlockPos();
        
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    
                    if (BlockUtil.isHole(pos)) {
                        RenderUtil.drawBlockBox(event.getMatrices(), pos, 0.0f, 1.0f, 0.0f, 0.3f);
                    }
                }
            }
        }
        
        RenderUtil.endRender();
    }
}