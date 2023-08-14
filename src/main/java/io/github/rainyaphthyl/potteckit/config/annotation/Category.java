package io.github.rainyaphthyl.potteckit.config.annotation;

/**
 * This enum is referenced for config tabs
 */
public enum Category {
    GENERIC("Generic", "generic"),
    /**
     * Helping players to see more info
     */
    METER("Meters", "meter"),
    /**
     * Helping players with game operations
     */
    ACTION("Actions", "action"),
    FIX("Fixes", "fix"),
    TWEAK("Tweaks", "tweak"),
    YEET("Yeet", "yeet"),
    WITH_SERVER("NeedServer", "server"),
    /**
     * Destructive to some vanilla features, causing players to forget the vanilla operations and refuse to redstone with them; Or solving some vanilla problems that could have been solved with vanilla technologies.
     */
    NOT_VANILLA("NotVanilla", "vanilless"),
    /**
     * Making some manual operations so convenient that players refuse to design automatic machines for them.
     */
    CHEATING("Cheating", "cheat");
    public final String name;
    /**
     * {@code "potteckit.config.tab." + key (lowercase)}
     */
    public final String key;

    Category(String name, String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public String toString() {
        return name;
    }
}