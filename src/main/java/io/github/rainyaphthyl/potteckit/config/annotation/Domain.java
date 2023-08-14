package io.github.rainyaphthyl.potteckit.config.annotation;

/**
 * This enum is referenced for config tabs
 */
public enum Domain {
    GENERIC,
    /**
     * Helping players to see more info
     */
    METER,
    /**
     * Helping players with game operations
     */
    ACTION,
    FIX,
    TWEAK,
    YEET,
    WITH_SERVER,
    /**
     * Destructive to some vanilla features, causing players to forget the vanilla operations and refuse to redstone with them; Or solving some vanilla problems that could have been solved with vanilla technologies.
     */
    NOT_VANILLA,
    /**
     * Making some manual operations so convenient that players refuse to design automatic machines for them.
     */
    CHEATING
}
