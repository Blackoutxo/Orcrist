package me.blackout.orcrist.systems.modules;

public enum Category {
    All("All", "icons/category/enabled.png"), Combat("Combat", "icons/category/combat.png"), Movement("Movement", "icons/category/movement.png"), Render("Render", "icons/category/visual.png"), World("World", "icons/category/world.png");

    public final String name, path;
    Category(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
