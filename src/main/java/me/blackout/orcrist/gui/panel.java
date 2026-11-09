package me.blackout.orcrist.gui;

import me.blackout.orcrist.settings.Setting;
import me.blackout.orcrist.systems.modules.Category;
import me.blackout.orcrist.systems.modules.Module;
import me.blackout.orcrist.systems.modules.Modules;
import me.blackout.orcrist.utils.render.Renderer2D;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.util.*;
import java.util.List;

public class panel {
    private final int Width = 400, Height = 300;
    private final int Sidebar_W = 50, Row_h = 25, padding = 4;

    // Colors
    private final Color BG = new Color(25, 25, 30);
    private final Color SIDEBAR = new Color(30, 30, 38);
    private final Color ENABLED = new Color(78, 67, 96);
    private final Color TEXT = new Color(255, 255, 255);

    private final GUI gui;
    public int x, y;

    private boolean dragging;
    private int dragX, dragY;

    private int scrollRow;

    private Category category = Category.All; // Default category
    private String search = "";

    private final Set<Module> expanded = new HashSet<>();

    private record Row(Module module, Setting<?> setting) {}

    public panel(GUI gui, int x, int y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
    }

    // Layout
    private int listX() { return x + Sidebar_W + padding; }
    private int listY() { return y + 20 + Height + padding; }
    private int listW() { return Width - Sidebar_W - padding; }
    private int visibleRows() { return (y - Height - padding - listY()) / (Row_h + 2); } // No overflow of modules

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

    }


}
