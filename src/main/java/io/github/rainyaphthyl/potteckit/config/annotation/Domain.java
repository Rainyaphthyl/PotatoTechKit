package io.github.rainyaphthyl.potteckit.config.annotation;

public enum Domain {
    /**
     * Main menu and mods of mods
     */
    GENERIC(Category.GENERIC),
    /**
     * Helping players to see more info
     */
    METER(Category.METER),
    /**
     * Helping players with game operations
     */
    ACTION(Category.ACTION),
    FIX(Category.FIX),
    TWEAK(Category.TWEAK),
    YEET(Category.YEET);
    public final Category category;

    Domain(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category.toString();
    }
}
