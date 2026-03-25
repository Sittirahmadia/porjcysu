package com.example.novaclient.module.modules.combat;

import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.module.setting.RangeSetting;
import org.lwjgl.glfw.GLFW;

public class Hitboxes extends Module {
    private static Hitboxes instance;

    public final RangeSetting expansion = new RangeSetting("Expansion", 0, 2, 0.3, 0.3, 0.01);

    public Hitboxes() {
        super("Hitboxes", "Expands entity hitboxes to make hitting easier", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
        addSettings(expansion);
        instance = this;
    }

    public static Hitboxes getInstance() {
        return instance;
    }

    public static float getExpansion() {
        if (instance != null && instance.isEnabled()) {
            return (float) instance.expansion.getMinValue();
        }
        return 0f;
    }
}