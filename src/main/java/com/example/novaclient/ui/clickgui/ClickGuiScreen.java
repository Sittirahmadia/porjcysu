package com.example.novaclient.ui.clickgui;

import com.example.novaclient.NovaClient;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import com.example.novaclient.module.setting.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEADER_HEIGHT = 18;
    private static final int MODULE_HEIGHT = 16;
    private static final int SETTING_HEIGHT = 14;
    
    private final List<Panel> panels = new ArrayList<>();
    private Panel draggingPanel = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    
    private final Map<Module, NumberSettingSlider> activeSliders = new HashMap<>();
    
    public ClickGuiScreen() {
        super(Text.literal("Nova Client"));
        initPanels();
    }
    
    private void initPanels() {
        int x = 10;
        int y = 10;
        
        for (Category category : Category.values()) {
            panels.add(new Panel(category, x, y));
            x += PANEL_WIDTH + 10;
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            
            if (panel.isMouseOverHeader((int) mouseX, (int) mouseY)) {
                draggingPanel = panel;
                dragOffsetX = (int) mouseX - panel.x;
                dragOffsetY = (int) mouseY - panel.y;
                panels.remove(i);
                panels.add(panel);
                return true;
            }
            
            if (panel.expanded && panel.isMouseOver((int) mouseX, (int) mouseY)) {
                if (panel.handleClick((int) mouseX, (int) mouseY)) {
                    return true;
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingPanel = null;
            activeSliders.clear();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingPanel != null) {
            draggingPanel.x = (int) mouseX - dragOffsetX;
            draggingPanel.y = (int) mouseY - dragOffsetY;
            return true;
        }
        
        for (NumberSettingSlider slider : activeSliders.values()) {
            slider.updateValue((int) mouseX);
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private class Panel {
        private final Category category;
        private int x;
        private int y;
        private boolean expanded = true;
        private final List<ModuleButton> moduleButtons = new ArrayList<>();
        
        public Panel(Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
            
            for (Module module : NovaClient.getInstance().getModuleManager().getModulesByCategory(category)) {
                moduleButtons.add(new ModuleButton(module));
            }
        }
        
        public void render(DrawContext context, int mouseX, int mouseY) {
            int currentY = y;
            
            context.fill(x, currentY, x + PANEL_WIDTH, currentY + PANEL_HEADER_HEIGHT, 0xF0000000);
            context.drawBorder(x, currentY, PANEL_WIDTH, PANEL_HEADER_HEIGHT, category.getColor());
            
            String title = category.getName();
            int titleWidth = textRenderer.getWidth(title);
            context.drawText(textRenderer, title, x + (PANEL_WIDTH - titleWidth) / 2, currentY + 5, 0xFFFFFFFF, false);
            
            currentY += PANEL_HEADER_HEIGHT;
            
            if (expanded) {
                int panelHeight = 0;
                for (ModuleButton button : moduleButtons) {
                    panelHeight += MODULE_HEIGHT;
                    if (button.expanded) {
                        panelHeight += button.getExpandedHeight();
                    }
                }
                
                context.fill(x, currentY, x + PANEL_WIDTH, currentY + panelHeight, 0xE0101010);
                
                for (ModuleButton button : moduleButtons) {
                    button.render(context, x, currentY, mouseX, mouseY);
                    currentY += MODULE_HEIGHT;
                    if (button.expanded) {
                        currentY += button.getExpandedHeight();
                    }
                }
            }
        }
        
        public boolean isMouseOverHeader(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y && mouseY <= y + PANEL_HEADER_HEIGHT;
        }
        
        public boolean isMouseOver(int mouseX, int mouseY) {
            int height = PANEL_HEADER_HEIGHT;
            if (expanded) {
                for (ModuleButton button : moduleButtons) {
                    height += MODULE_HEIGHT;
                    if (button.expanded) height += button.getExpandedHeight();
                }
            }
            return mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y && mouseY <= y + height;
        }
        
        public boolean handleClick(int mouseX, int mouseY) {
            int currentY = y + PANEL_HEADER_HEIGHT;
            
            for (ModuleButton button : moduleButtons) {
                if (mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT) {
                    if (mouseX >= x + PANEL_WIDTH - 12) {
                        button.expanded = !button.expanded;
                        return true;
                    }
                    button.module.toggle();
                    return true;
                }
                currentY += MODULE_HEIGHT;
                
                if (button.expanded) {
                    if (button.handleSettingClick(mouseX, mouseY, currentY, x)) {
                        return true;
                    }
                    currentY += button.getExpandedHeight();
                }
            }
            
            return false;
        }
    }
    
    private class ModuleButton {
        private final Module module;
        private boolean expanded = false;
        
        public ModuleButton(Module module) {
            this.module = module;
        }
        
        public void render(DrawContext context, int x, int y, int mouseX, int mouseY) {
            boolean hovered = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y && mouseY <= y + MODULE_HEIGHT;
            int bgColor = module.isEnabled() ? 0x80404040 : (hovered ? 0x40303030 : 0x20202020);
            
            context.fill(x, y, x + PANEL_WIDTH, y + MODULE_HEIGHT, bgColor);
            
            int textColor = module.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA;
            context.drawText(textRenderer, module.getName(), x + 4, y + 4, textColor, false);
            
            if (!module.getSettings().isEmpty()) {
                String arrow = expanded ? "v" : ">";
                context.drawText(textRenderer, arrow, x + PANEL_WIDTH - 10, y + 4, 0xFFFFFFFF, false);
            }
            
            if (expanded && !module.getSettings().isEmpty()) {
                renderSettings(context, x, y + MODULE_HEIGHT, mouseX, mouseY);
            }
        }
        
        private void renderSettings(DrawContext context, int x, int y, int mouseX, int mouseY) {
            int currentY = y;
            
            for (Setting<?> setting : module.getSettings()) {
                boolean hovered = mouseX >= x && mouseX <= x + PANEL_WIDTH && 
                                mouseY >= currentY && mouseY <= currentY + SETTING_HEIGHT;
                
                context.fill(x, currentY, x + PANEL_WIDTH, currentY + SETTING_HEIGHT, 
                           hovered ? 0x30505050 : 0x20303030);
                
                if (setting instanceof BooleanSetting bool) {
                    String value = bool.getValue() ? "ON" : "OFF";
                    int valueColor = bool.getValue() ? 0xFF00FF00 : 0xFFFF0000;
                    context.drawText(textRenderer, setting.getName(), x + 8, currentY + 3, 0xFFCCCCCC, false);
                    context.drawText(textRenderer, value, x + PANEL_WIDTH - textRenderer.getWidth(value) - 8, 
                                   currentY + 3, valueColor, false);
                } else if (setting instanceof NumberSetting num) {
                    context.drawText(textRenderer, setting.getName(), x + 8, currentY + 3, 0xFFCCCCCC, false);
                    
                    double percentage = (num.getValue() - num.getMin()) / (num.getMax() - num.getMin());
                    int barWidth = (int) ((PANEL_WIDTH - 16) * percentage);
                    context.fill(x + 8, currentY + SETTING_HEIGHT - 4, x + PANEL_WIDTH - 8, currentY + SETTING_HEIGHT - 2, 0xFF303030);
                    context.fill(x + 8, currentY + SETTING_HEIGHT - 4, x + 8 + barWidth, currentY + SETTING_HEIGHT - 2, 0xFF4080FF);
                    
                    String valueText = String.format("%.2f", num.getValue());
                    int valueWidth = textRenderer.getWidth(valueText);
                    context.drawText(textRenderer, valueText, x + PANEL_WIDTH - valueWidth - 8, currentY + 3, 0xFF88AAFF, false);
                } else if (setting instanceof ModeSetting mode) {
                    context.drawText(textRenderer, setting.getName(), x + 8, currentY + 3, 0xFFCCCCCC, false);
                    String value = mode.getValue();
                    int valueWidth = textRenderer.getWidth(value);
                    context.drawText(textRenderer, value, x + PANEL_WIDTH - valueWidth - 8, currentY + 3, 0xFFFFAA00, false);
                } else if (setting instanceof KeybindSetting key) {
                    String keyName = key.getKeyCode() == 0 ? "NONE" : GLFW.glfwGetKeyName(key.getKeyCode(), 0);
                    if (keyName == null) keyName = "KEY " + key.getKeyCode();
                    keyName = keyName.toUpperCase();
                    context.drawText(textRenderer, setting.getName(), x + 8, currentY + 3, 0xFFCCCCCC, false);
                    int valueWidth = textRenderer.getWidth(keyName);
                    context.drawText(textRenderer, keyName, x + PANEL_WIDTH - valueWidth - 8, currentY + 3, 0xFFFF88FF, false);
                }
                
                currentY += SETTING_HEIGHT;
            }
        }
        
        public boolean handleSettingClick(int mouseX, int mouseY, int startY, int panelX) {
            int currentY = startY;
            
            for (Setting<?> setting : module.getSettings()) {
                if (mouseY >= currentY && mouseY <= currentY + SETTING_HEIGHT) {
                    if (setting instanceof BooleanSetting bool) {
                        bool.setValue(!bool.getValue());
                        return true;
                    } else if (setting instanceof NumberSetting num) {
                        NumberSettingSlider slider = new NumberSettingSlider(num, panelX + 8, panelX + PANEL_WIDTH - 8);
                        activeSliders.put(module, slider);
                        slider.updateValue(mouseX);
                        return true;
                    } else if (setting instanceof ModeSetting mode) {
                        mode.cycle();
                        return true;
                    }
                }
                currentY += SETTING_HEIGHT;
            }
            
            return false;
        }
        
        public int getExpandedHeight() {
            return module.getSettings().size() * SETTING_HEIGHT;
        }
    }
    
    private static class NumberSettingSlider {
        private final NumberSetting setting;
        private final int minX;
        private final int maxX;
        
        public NumberSettingSlider(NumberSetting setting, int minX, int maxX) {
            this.setting = setting;
            this.minX = minX;
            this.maxX = maxX;
        }
        
        public void updateValue(int mouseX) {
            double percentage = MathHelper.clamp((mouseX - minX) / (double) (maxX - minX), 0, 1);
            double value = setting.getMin() + (setting.getMax() - setting.getMin()) * percentage;
            setting.setValue(value);
        }
    }
}