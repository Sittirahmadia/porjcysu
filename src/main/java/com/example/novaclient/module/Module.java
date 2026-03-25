package com.example.novaclient.module;

import com.example.novaclient.NovaClient;
import com.example.novaclient.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private int key;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category, int key) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.key = key;
    }

    public Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
            NovaClient.getInstance().getEventBus().register(this);
        } else {
            NovaClient.getInstance().getEventBus().unregister(this);
            onDisable();
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public boolean isNull() {
        return mc.player == null || mc.world == null;
    }

    protected void addSettings(Setting<?>... s) {
        settings.addAll(Arrays.asList(s));
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }
}