package me.blackout.orcrist.gui;

import me.blackout.orcrist.systems.Category;

public class panel {
    private static final int width = 400, height = 300;

    private final GUI gui;
    public final int x, y;

    private boolean dragging;
    private int dragX, dragY;

    private Category category = Category.ALL; // Default category

    public panel(GUI gui, int x, int y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
    }

}
