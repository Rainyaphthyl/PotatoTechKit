package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * A timestamp containing gametick, dimension, phase, and other arguments.
 */
public class TickRecord implements Comparable<TickRecord> {
    /**
     * The global tick counter (int), increasing <b>between</b> ticks.
     */
    public final long tickOrdinal;
    /**
     * The dimensional game time output from Command Blocks, increasing <b>during</b> each tick.
     */
    public final long gameTime;
    public final DimensionType dimensionType;
    public final GamePhase gamePhase;
    /**
     * For Block Events: Event Depth;
     * <p>
     * For Tile Ticks: Priority;
     */
    public final Object[] arguments;

    public TickRecord(long tickOrdinal, long gameTime, DimensionType dimensionType, GamePhase gamePhase, Object... arguments) throws NullPointerException {
        this.tickOrdinal = tickOrdinal;
        this.gameTime = gameTime;
        this.gamePhase = Objects.requireNonNull(gamePhase);
        this.dimensionType = gamePhase.dimensional ? Objects.requireNonNull(dimensionType) : null;
        this.arguments = checkArguments(arguments);
    }

    /**
     * Check the arguments for Tile Ticks, Block Events, etc.
     */
    @Nonnull
    private Object[] checkArguments(Object[] argumentsIn) {
        return argumentsIn == null ? new Object[0] : argumentsIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TickRecord)) return false;
        TickRecord that = (TickRecord) obj;
        if (tickOrdinal != that.tickOrdinal) return false;
        if (gameTime != that.gameTime) return false;
        if (dimensionType != that.dimensionType) return false;
        if (gamePhase != that.gamePhase) return false;
        return Arrays.deepEquals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = (int) (tickOrdinal ^ (tickOrdinal >>> 32));
        result = 31 * result + (int) (gameTime ^ (gameTime >>> 32));
        result = 31 * result + (dimensionType != null ? dimensionType.hashCode() : 0);
        result = 31 * result + gamePhase.hashCode();
        // Do not use deepHashCode
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    @Override
    public int compareTo(@Nonnull TickRecord that) {
        if (this == that) return 0;
        if (tickOrdinal != that.tickOrdinal) return Long.compare(tickOrdinal, that.tickOrdinal);
        if (gamePhase != that.gamePhase) return gamePhase.compareTo(that.gamePhase);
        if (gamePhase.dimensional && dimensionType != that.dimensionType)
            return dimensionType.compareTo(that.dimensionType);
        return 0;
    }
}
