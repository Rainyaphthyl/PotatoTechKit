package io.github.rainyaphthyl.potteckit.config.annotation;

/**
 * This enum is referenced for config tabs
 */
public enum Domain {
    GENERIC("Generic", "generic"),
    /**
     * Helping players to see more info
     */
    METER("Meter", "meter"),
    /**
     * Helping players with game operations
     */
    ACTION("Action", "action"),
    FIX("Fix", "fix"),
    TWEAK("Tweak", "tweak"),
    YEET("Yeet", "yeet"),
    WITH_SERVER("SinglePlayerOnly", "server"),
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

    Domain(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
