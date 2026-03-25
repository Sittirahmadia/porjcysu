package com.example.novaclient;

import com.example.novaclient.event.EventBus;
import com.example.novaclient.manager.ModuleManager;
import com.example.novaclient.manager.RotationManager;
import com.example.novaclient.manager.FriendManager;
import com.example.novaclient.ui.clickgui.ClickGuiScreen;
import com.example.novaclient.ui.keybinds.KeybindsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "novaclient";
    public static final String MOD_NAME = "Nova Client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static NovaClient instance;
    private EventBus eventBus;
    private ModuleManager moduleManager;
    private RotationManager rotationManager;
    private FriendManager friendManager;
    private KeyBinding clickGuiKey;
    private KeyBinding keybindsKey;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        
        LOGGER.info("Initializing {} v2.0.0", MOD_NAME);
        
        eventBus = new EventBus();
        rotationManager = new RotationManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.novaclient.clickgui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.novaclient"
        ));
        
        keybindsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.novaclient.keybinds",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            "category.novaclient"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clickGuiKey.wasPressed() && client.currentScreen == null) {
                client.setScreen(new ClickGuiScreen());
            }
            if (keybindsKey.wasPressed() && client.currentScreen == null) {
                client.setScreen(new KeybindsScreen());
            }
        });
        
        LOGGER.info("{} initialized successfully!", MOD_NAME);
    }
    
    public static NovaClient getInstance() {
        return instance;
    }
    
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public RotationManager getRotationManager() {
        return rotationManager;
    }
    
    public FriendManager getFriendManager() {
        return friendManager;
    }
}