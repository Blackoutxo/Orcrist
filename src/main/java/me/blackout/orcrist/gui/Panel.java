package me.blackout.orcrist.gui;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.KeybindSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Panel {
    private final int Width = 400, Height = 300;
    private final int Sidebar_W = 80, Row_h = 25, padding = 4;

    // Colors
    private final Color ACCENT = new Color(90, 131, 255);
    private final Color BG = new Color(25, 25, 30);
    private final Color SIDEBAR = new Color(30, 30, 38);
    private final Color HOVER = new Color(133, 121, 121);
    private final Color ENABLED = new Color(78, 67, 96);
    private final Color TEXT = new Color(255, 255, 255);

    private final GUI gui;
    public int x, y;

    private boolean dragging;
    private int dragX, dragY;

    private int scrollRow;

    private Category category = Category.All; // Default category
    private String search = "";
    private boolean searchFocused;

    private final Set<Module> expanded = new HashSet<>();
    private KeybindSetting listener;

    private record Row(Module module, Setting<?> setting) {}

    public Panel(GUI gui, int x, int y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
    }

    // Layout
    private int listX() { return x + Sidebar_W + padding; }
    private int listY() { return y + 38 + padding * 2; }
    private int listW() { return Width - Sidebar_W - padding; }
    private int visibleRows() { return (y + Height - padding - listY()) / (Row_h + 2); } // No overflow of modules

    // Bounds check
    private boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private List<Row> buildRows() {
        List<Row> rows = new ArrayList<>();
        for (Module m : Modules.get(category, search)) {
            rows.add(new Row(m, null));
            if (expanded.contains(m)) {
                for (Setting<?> s : m.getSettings()) rows.add(new Row(m, s));
            }
        }
        return rows;
    }

    // Render
    public void render(GuiGraphicsExtractor g, int mx, int my) {
        if (dragging) {
            x = mx - dragX;
            y = my - dragY;
        }

        Renderer2D.quad(g, x, y, Width, Height, BG);
        Renderer2D.quad(g, x, y, Width, 20, new Color(35, 35, 45));
        Renderer2D.text(g, "Orcrist", x + 200, y + 6, TEXT);

        // Sidebar
        Renderer2D.quad(g, x, y + 20, Sidebar_W, Height - 20, SIDEBAR);
        int cy = y + 20 + padding;

        for (Category c : Category.values()) {
            boolean hover = inside(mx, my, x + padding, cy, Sidebar_W - padding * 2, Row_h);

            Renderer2D.quad(g, x + padding, cy, Sidebar_W - padding * 2, Row_h, hover ? HOVER : SIDEBAR);

            if (c == category) Renderer2D.quad(g, x + padding, cy, 2, Row_h, ACCENT);
            Renderer2D.text(g, c.name, x + padding + 5, cy + 8, c == category ? TEXT : TEXT.darker());
            cy += Row_h + 2;
        }

        // search bar
        int sy = y + 20 + padding;
        Renderer2D.quad(g, listX(), sy, listW() - 3, 18, searchFocused ? HOVER : new Color(40, 40, 50));
        String shown = search.isEmpty() && !searchFocused ? "Search........." : search + "_";
        Renderer2D.text(g, shown, listX() + 4, sy + 5, search.isEmpty() ? TEXT.darker() : TEXT);

        // Rows
        List<Row> rows = buildRows();
        scrollRow = Math.max(0, Math.min(scrollRow, Math.max(0, rows.size() - visibleRows())));

        int ry = listY();

        for (int i = scrollRow; i < Math.min(rows.size(), scrollRow + visibleRows()); i++) {
            Row row = rows.get(i);

            if (row.setting == null) renderModuleRow(g, row.module(), ry, mx, my);
            else renderSettingsRow(g, row.setting(), ry, mx, my);
            ry += Row_h + 2;
        }
    }

    private void renderModuleRow(GuiGraphicsExtractor g, Module m, int ry, int mx, int my) {
        boolean hover = inside(mx, my, listX(), ry, listW(), Row_h);

        Renderer2D.quad(g, listX(), ry, listW() - 3, Row_h, hover ? HOVER : BG);
        Renderer2D.quad(g, listX(), ry, 3, Row_h, m.isEnabled() ? ENABLED : BG);
        Renderer2D.text(g, m.name, x + 200, ry + 7, TEXT);

        if (!m.getSettings().isEmpty()) {
            Renderer2D.text(g, expanded.contains(m) ? "v" : ">", listX() + listW() - 12, ry + 6, TEXT);
        }
    }

    private void renderSettingsRow(GuiGraphicsExtractor g, Setting<?> s, int ry, int mx, int my) {
         int rx = listX() + 10, rw = listW() - 10;
         boolean hover = inside(mx, my, rx, ry, rw, Row_h);
         Renderer2D.quad(g, rx, ry, rw, Row_h, hover ? HOVER : BG);

        // Settings
        if (s instanceof BoolSetting b) {
            Renderer2D.text(g, b.name, rx + 6, ry + 6, TEXT);
            Renderer2D.text(g, b.get() ? "On" : "Off", rx + rw - 24, ry + 6, b.get() ? ENABLED : TEXT.darker());
        }
    }

    // Input
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!inside(mouseX, mouseY, x, y, Width, Height)) return false;

        if (inside(mouseX, mouseY, x, y, Width, 20)) {
            dragging = true;
            dragX = (int) mouseX - x;
            dragY = (int) mouseY - y;
            return true;
        }

        int cy = y + 20 + padding;
        for (Category c : Category.values()) {
            if (inside(mouseX, mouseY, x + padding, cy, Sidebar_W - padding * 2, Row_h)) {
                category = c;
                scrollRow = 0;
                return true;
            }
            cy += Row_h + 2;
        }

        searchFocused = inside(mouseX, mouseY, listX(), y + 20 + padding, listW(), 18);
        if (searchFocused) return true;

        List<Row> rows = buildRows();
        int ry = listY();

        for (int i = scrollRow; i < Math.min(rows.size(), scrollRow + visibleRows()); i++) {
            Row row = rows.get(i);

            if (inside(mouseX, mouseY, listX(), ry, listW(), Row_h)) {
                KeybindSetting prev = listener;
                listener = null;
                if (row.setting == null) {
                    if (button == 1) row.module().toggle();
                    if (button == 3 && !row.module().getSettings().isEmpty()) {
                        if (!expanded.remove(row.module())) expanded.add(row.module());
                    }
                } else if (button == 1) {
                    Setting<?> s = row.setting();

                    if (s instanceof BoolSetting b) b.toggle();
                } else if (button == 3) {
                    row.setting.reset();
                }
                return true;
            }
            ry += Row_h + 2;
        }
        return true;
    }

    public boolean keyPressed(int key) {
        if (listener == null) return false;

        if (key == InputConstants.KEY_ESCAPE) {}
        else if (key == InputConstants.KEY_BACKSPACE || key == InputConstants.KEY_DELETE) listener.set(KeybindSetting.NONE);
        else listener.set(key);

        listener = null;
        return true;
    }

    public boolean charTyped(char c) {
        if (!searchFocused) return false;
        if (c >= 32 && search.length() < 24) {
            search += c;
            scrollRow = 0;
        }
        return true;
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void mouseScrolled(double amount) {
        scrollRow -= (int) Math.signum(amount);
    }

    public boolean backSpace() {
        if (!searchFocused || search.isEmpty()) return false;

        search = search.substring(0, search.length() - 1);
        scrollRow = 0;

        return true;
    }

    public boolean isTyping() {
        return searchFocused;
    }
}
