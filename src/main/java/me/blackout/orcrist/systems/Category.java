package me.blackout.orcrist.systems;

public enum Category {
    All("All"), Combat("Combat"), Movement("Movement"), Render("Render"), World("World");

    public final String name;
    Category(String name) { this.name = name; }
}
