package com.example.novaclient.ui.keybinds;

import com.example.novaclient.NovaClient;
import com.example.novaclient.module.Category;
import com.example.novaclient.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeybindsScreen extends Screen {
    private static final int ENTRY_HEIGHT = 30;
    private static final int ENTRY_MARGIN = 5;
    private static final int CATEGORY_HEIGHT = 25;
    
    private Category selectedCategory = Category.COMBAT;
    private Module listeningModule = null;
    private String searchQuery = "";
    private boolean searchFocused = false;
    private int scrollOffset = 0;
    private final Map<Category, List<Module>> categoryModules = new HashMap<>();
    
    public KeybindsScreen() {
        super(Text.literal("Keybinds"));
        loadModules();
    }
    
    private void loadModules() {
        for (Category category : Category.values()) {
            categoryModules.put(category, NovaClient.getInstance().getModuleManager().getModulesByCategory(category));
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        int centerX = width / 2;
        int panelWidth = 500;
        int panelX = centerX - panelWidth / 2;
        int panelY = 50;
        int panelHeight = height - 100;
        
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xE0000000);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF333333);
        
        String title = "Keybind Manager";
        int titleWidth = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, centerX - titleWidth / 2, panelY + 10, 0xFFFFFFFF, true);
        
        renderSearchBar(context, panelX, panelY + 30, panelWidth);
        
        renderCategoryTabs(context, panelX, panelY + 60, panelWidth, mouseX, mouseY);
        
        renderModuleList(context, panelX, panelY + 90, panelWidth, panelHeight - 90, mouseX, mouseY);
        
        if (listeningModule != null) {
            renderListeningOverlay(context);
        }
    }
    
    private void renderSearchBar(DrawContext context, int x, int y, int width) {
        int barHeight = 20;
        context.fill(x + 10, y, x + width - 10, y + barHeight, 0x80000000);
        context.drawBorder(x + 10, y, width - 20, barHeight, searchFocused ? 0xFF4080FF : 0xFF333333);
        
        String displayText = searchQuery.isEmpty() ? "Search..." : searchQuery;
        int textColor = searchQuery.isEmpty() ? 0xFF666666 : 0xFFFFFFFF;
        context.drawText(textRenderer, displayText, x + 15, y + 6, textColor, false);
        
        if (searchFocused && System.currentTimeMillis() % 1000 < 500) {
            int cursorX = x + 15 + textRenderer.getWidth(searchQuery);
            context.fill(cursorX, y + 4, cursorX + 1, y + 16, 0xFFFFFFFF);
        }
    }
    
    private void renderCategoryTabs(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        int tabWidth = width / Category.values().length;
        int currentX = x;
        
        for (Category category : Category.values()) {
            boolean selected = category == selectedCategory;
            boolean hovered = mouseX >= currentX && mouseX <= currentX + tabWidth && 
                            mouseY >= y && mouseY <= y + CATEGORY_HEIGHT;
            
            int bgColor = selected ? 0xC0303030 : (hovered ? 0x80202020 : 0x60101010);
            context.fill(currentX, y, currentX + tabWidth, y + CATEGORY_HEIGHT, bgColor);
            
            if (selected) {
                context.fill(currentX, y + CATEGORY_HEIGHT - 2, currentX + tabWidth, y + CATEGORY_HEIGHT, category.getColor());
            }
            
            String name = category.getName();
            int textWidth = textRenderer.getWidth(name);
            int textColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
            context.drawText(textRenderer, name, currentX + (tabWidth - textWidth) / 2, y + 8, textColor, false);
            
            currentX += tabWidth;
        }
    }
    
    private void renderModuleList(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        List<Module> modules = getFilteredModules();
        
        context.enableScissor(x, y, x + width, y + height);
        
        int currentY = y + 5 - scrollOffset;
        
        for (Module module : modules) {
            if (currentY + ENTRY_HEIGHT < y) {
                currentY += ENTRY_HEIGHT + ENTRY_MARGIN;
                continue;
            }
            if (currentY > y + height) break;
            
            boolean hovered = mouseX >= x + 10 && mouseX <= x + width - 10 && 
                            mouseY >= currentY && mouseY <= currentY + ENTRY_HEIGHT;
            
            int bgColor = hovered ? 0x60404040 : 0x40202020;
            context.fill(x + 10, currentY, x + width - 10, currentY + ENTRY_HEIGHT, bgColor);
            context.drawBorder(x + 10, currentY, width - 20, ENTRY_HEIGHT, 0xFF333333);
            
            int moduleColor = module.isEnabled() ? 0xFFFFFFFF : 0xFF888888;
            context.drawText(textRenderer, module.getName(), x + 20, currentY + 5, moduleColor, false);
            
            String keyName = getKeyName(module.getKey());
            int keyColor = module.getKey() == 0 ? 0xFF666666 : 0xFF4080FF;
            int keyWidth = textRenderer.getWidth(keyName);
            int keyX = x + width - 20 - keyWidth;
            int keyY = currentY + 5;
            
            context.fill(keyX - 5, keyY - 2, keyX + keyWidth + 5, keyY + 12, 0x60000000);
            context.drawText(textRenderer, keyName, keyX, keyY, keyColor, false);
            
            if (module.getDescription() != null && !module.getDescription().isEmpty()) {
                context.drawText(textRenderer, module.getDescription(), x + 20, currentY + 17, 0xFF666666, false);
            }
            
            currentY += ENTRY_HEIGHT + ENTRY_MARGIN;
        }
        
        context.disableScissor();
    }
    
    private void renderListeningOverlay(DrawContext context) {
        context.fill(0, 0, width, height, 0xA0000000);
        
        int boxWidth = 300;
        int boxHeight = 100;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xF0000000);
        context.drawBorder(boxX, boxY, boxWidth, boxHeight, 0xFF4080FF);
        
        String text1 = "Listening for key...";
        String text2 = "Press ESC to cancel";
        String text3 = "Module: " + listeningModule.getName();
        
        int text1Width = textRenderer.getWidth(text1);
        int text2Width = textRenderer.getWidth(text2);
        int text3Width = textRenderer.getWidth(text3);
        
        context.drawText(textRenderer, text3, boxX + (boxWidth - text3Width) / 2, boxY + 20, 0xFFFFFFFF, true);
        context.drawText(textRenderer, text1, boxX + (boxWidth - text1Width) / 2, boxY + 45, 0xFF4080FF, true);
        context.drawText(textRenderer, text2, boxX + (boxWidth - text2Width) / 2, boxY + 70, 0xFF666666, false);
    }
    
    private List<Module> getFilteredModules() {
        List<Module> result = new ArrayList<>();
        List<Module> source = categoryModules.get(selectedCategory);
        
        if (searchQuery.isEmpty()) {
            result.addAll(source);
        } else {
            String query = searchQuery.toLowerCase();
            for (Module module : source) {
                if (module.getName().toLowerCase().contains(query)) {
                    result.add(module);
                }
            }
        }
        
        return result;
    }
    
    private String getKeyName(int keyCode) {
        if (keyCode == 0) return "NONE";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name == null) {
            return switch (keyCode) {
                case GLFW.GLFW_KEY_ESCAPE -> "ESC";
                case GLFW.GLFW_KEY_SPACE -> "SPACE";
                case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
                case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
                case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
                case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
                case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
                case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
                default -> "KEY" + keyCode;
            };
        }
        return name.toUpperCase();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        if (listeningModule != null) {
            listeningModule = null;
            return true;
        }
        
        int centerX = width / 2;
        int panelWidth = 500;
        int panelX = centerX - panelWidth / 2;
        int panelY = 50;
        
        int searchY = panelY + 30;
        if (mouseX >= panelX + 10 && mouseX <= panelX + panelWidth - 10 &&
            mouseY >= searchY && mouseY <= searchY + 20) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }
        
        int tabY = panelY + 60;
        if (mouseY >= tabY && mouseY <= tabY + CATEGORY_HEIGHT) {
            int tabWidth = panelWidth / Category.values().length;
            int currentX = panelX;
            for (Category category : Category.values()) {
                if (mouseX >= currentX && mouseX <= currentX + tabWidth) {
                    selectedCategory = category;
                    scrollOffset = 0;
                    return true;
                }
                currentX += tabWidth;
            }
        }
        
        int listY = panelY + 90;
        int panelHeight = height - 100;
        int currentY = listY + 5 - scrollOffset;
        
        for (Module module : getFilteredModules()) {
            if (currentY + ENTRY_HEIGHT >= listY && currentY <= listY + panelHeight - 90) {
                if (mouseX >= panelX + 10 && mouseX <= panelX + panelWidth - 10 &&
                    mouseY >= currentY && mouseY <= currentY + ENTRY_HEIGHT) {
                    listeningModule = module;
                    return true;
                }
            }
            currentY += ENTRY_HEIGHT + ENTRY_MARGIN;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, getFilteredModules().size() * (ENTRY_HEIGHT + ENTRY_MARGIN) - (height - 100 - 90));
        scrollOffset -= (int) (verticalAmount * 20);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningModule = null;
            } else if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                listeningModule.setKey(0);
                listeningModule = null;
            } else {
                listeningModule.setKey(keyCode);
                listeningModule = null;
            }
            return true;
        }
        
        if (searchFocused) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchFocused = false;
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
        }
        
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !searchFocused) {
            close();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && chr < 127) {
            searchQuery += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}