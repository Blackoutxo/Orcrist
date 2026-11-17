package me.blackout.orcrist.gui;

import com.mojang.blaze3d.platform.InputConstants;
import me.blackout.orcrist.settings.BoolSetting;
import me.blackout.orcrist.settings.KeybindSetting;
import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import me.blackout.orcrist.utils.Utils;
import me.blackout.orcrist.utils.render.Renderer2D;
import me.blackout.orcrist.utils.render.text.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Panel {
    private final int Width = 400, Height = 250;
    private final int Sidebar_W = 90, Row_h = 25, padding = 4;

    // Colors
    private final Color ACCENT = new Color(90, 131, 255);
    private final Color BG = new Color(24, 24, 24);
    private final Color SIDEBAR = new Color(22, 22, 33);
    private final Color HOVER = new Color(133, 121, 121, 137);
    private final Color ENABLED = new Color(121, 100, 100, 255);
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

        Renderer2D.quadWH(g, x, y + 1, Width, Height, BG);
        Renderer2D.verticalGradient(g, x + 85, y + 1, x + Width - 2, y + Height - 2, new Color(32, 32, 47), new Color(69, 77, 101));

        // Sidebar
        Renderer2D.quadWH(g, x + 2, y + 2, 90, Height - 4, SIDEBAR);
        int cy = y + 20 + padding;

        for (Category c : Category.values()) {
            boolean hover = inside(mx, my, x + padding, cy, Sidebar_W - padding * 2, Row_h);

            Renderer2D.quadWH(g, x + padding, cy, Sidebar_W - padding * 2, Row_h, hover ? HOVER : SIDEBAR);

            if (c == category) Renderer2D.quadWH(g, x + padding, cy, 2, Row_h, ACCENT);
            Fonts.get(9).draw(g, c.name, x + padding + 5, cy + 8, c == category ? TEXT : TEXT.darker());
            cy += Row_h + 2;
        }

        // Header
        Renderer2D.quadWH(g, x + 92, y + 3, Width - 92, 28 - 1, new Color(22, 22, 33));
        Renderer2D.quadWH(g, x + 92, y + 2, Width - 92, 1, new Color(31, 31, 31));
        Renderer2D.verticalGradient(g, x + 92, y + 28, x + Width - 1, y + 33, new Color(142, 168, 224), new Color(46, 51, 73));

        Renderer2D.quadWH(g, x + 6, y + 3, 80, 0, new Color(74, 79, 101));

        Fonts.get(9).draw(g, "Orcrist", x + 200, y + 6, TEXT);

        // search bar
//        int sy = y + 30 + padding;
//        Renderer2D.quadWH(g, listX(), sy, listW() - 3, 18, searchFocused ? HOVER : new Color(40, 40, 50));
//        String shown = search.isEmpty() && !searchFocused ? "Search........." : search + "_";
//        Renderer2D.text(g, shown, listX() + 4, sy + 5, search.isEmpty() ? TEXT.darker() : TEXT);

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
        boolean hover = inside(mx, my, listX(), ry, 140, Row_h);

        Renderer2D.quadWH(g, listX(), ry, (int) Utils.animate(0, 140, 2), Row_h, hover ? HOVER : new Color(0, 0, 0, 0));
        Renderer2D.quadWH(g, listX(), ry, 3,(int) Utils.animate(0, Row_h, 1), m.isEnabled() ? ENABLED : BG);
        Fonts.get(12).draw(g, m.name, x + 200, ry + 7, TEXT);
    }

    private void renderSettingsRow(GuiGraphicsExtractor g, Setting<?> s, int ry, int mx, int my) {
         int rx = listX() + 140, rw = 140;
         boolean hover = inside(mx, my, rx, ry, rw, Row_h);
         Renderer2D.quadWH(g, rx, ry, rw, Row_h, hover ? HOVER : new Color(0, 0, 0, 0));

        // Settings
        if (s instanceof KeybindSetting k) {
            Fonts.get(9).draw(g, k.name, rx + 6, ry + 6, TEXT);

            String bind = ( listener == k ) ? "...." : k.label();
            Fonts.get(9).draw(g, bind, rx + rw - 6 - bind.length() * 6, ry + 6, TEXT);
        } else if (s instanceof BoolSetting b) {
            Fonts.get(9).draw(g, b.name, rx + 6, ry + 6, TEXT);
            Fonts.get(9).draw(g, b.get() ? "On" : "Off", rx + rw - 24, ry + 6, b.get() ? TEXT : TEXT.darker());
        }
    }

    // Input
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!inside(mouseX, mouseY, x, y, Width, Height)) return false;
        focusSearch(false);

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

        focusSearch(inside(mouseX, mouseY, listX(), y + 20 + padding, listW(), 18));
        if (searchFocused) return true;

        List<Row> rows = buildRows();
        int ry = listY();

        for (int i = scrollRow; i < Math.min(rows.size(), scrollRow + visibleRows()); i++) {
            Row row = rows.get(i);

            if (inside(mouseX, mouseY, listX(), ry, listW(), Row_h)) {
                KeybindSetting prev = listener;
                listener = null;
                if (row.setting == null) {
                    if (button == InputConstants.MOUSE_BUTTON_LEFT) row.module().toggle();
                    if (button == InputConstants.MOUSE_BUTTON_RIGHT && !row.module().getSettings().isEmpty()) {
                        if (!expanded.remove(row.module())) expanded.add(row.module());
                    }
                } else if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                    Setting<?> s = row.setting();

                    if (s instanceof BoolSetting b) b.toggle();
                    else if (s instanceof KeybindSetting k) listener = (prev == k) ? null : k;
                } else if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
                    row.setting.reset();
                }
                return true;
            }
            ry += Row_h + 2;
        }
        return true;
    }

    private void focusSearch(boolean focus) {
        if (searchFocused == focus) return;
        searchFocused = focus;
        gui.setTextInput(focus);
    }

    public boolean keyPressed(KeyEvent event) {
        if (listener == null) return false;

        if (event.key() == InputConstants.KEY_ESCAPE);
        else if (event.key() == InputConstants.KEY_BACKSPACE || event.key() == InputConstants.KEY_DELETE) listener.set(KeybindSetting.NONE);
        else listener.bind(event);

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
