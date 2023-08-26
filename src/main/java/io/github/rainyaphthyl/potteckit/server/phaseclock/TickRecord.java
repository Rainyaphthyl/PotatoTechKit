package io.github.rainyaphthyl.potteckit.server.phaseclock;

import io.github.rainyaphthyl.potteckit.server.phaseclock.subphase.SubPhase;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A timestamp containing gametick, dimension, phase, and other arguments.
 */
public class TickRecord implements Comparable<TickRecord> {
    /**
     * The global and <b>comparable</b> tick counter (int), increasing <b>between</b> ticks.
     */
    public final int tickOrdinal;
    /**
     * The dimensional game time output from Command Blocks, increasing <b>during</b> each tick.
     */
    public final long gameTime;
    public final DimensionType dimensionType;
    public final GamePhase gamePhase;
    /**
     * Sub-phases without the unique sub-tick ordinal.
     */
    public final SubPhase subPhase;
    /**
     * The unique sub-"sub-phase" ordinal, to be compared when the sub-phases are equal.
     */
    public final int eventOrdinal;

    private TickRecord(int tickOrdinal, long gameTime, DimensionType dimensionType, GamePhase gamePhase, SubPhase subPhase, int eventOrdinal) throws NullPointerException, IllegalArgumentException {
        this.tickOrdinal = tickOrdinal;
        this.gameTime = gameTime;
        this.gamePhase = Objects.requireNonNull(gamePhase);
        this.dimensionType = gamePhase.dimensional ? Objects.requireNonNull(dimensionType) : null;
        if (subPhase != null && subPhase.parentPhase() != gamePhase) {
            throw new IllegalArgumentException();
        }
        this.subPhase = subPhase;
        this.eventOrdinal = eventOrdinal;
    }

    @Nullable
    public static TickRecord getInstance(int tickOrdinal, long gameTime, DimensionType dimensionType, GamePhase gamePhase, SubPhase subPhase, int eventOrdinal) {
        try {
            return new TickRecord(tickOrdinal, gameTime, dimensionType, gamePhase, subPhase, eventOrdinal);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TickRecord)) return false;
        TickRecord that = (TickRecord) o;
        if (tickOrdinal != that.tickOrdinal) return false;
        if (eventOrdinal != that.eventOrdinal) return false;
        if (dimensionType != that.dimensionType) return false;
        if (gamePhase != that.gamePhase) return false;
        return Objects.equals(subPhase, that.subPhase);
    }

    @Override
    public int hashCode() {
        int result = tickOrdinal;
        result = 31 * result + (dimensionType != null ? dimensionType.hashCode() : 0);
        result = 31 * result + gamePhase.hashCode();
        result = 31 * result + (subPhase != null ? subPhase.hashCode() : 0);
        result = 31 * result + eventOrdinal;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(tickOrdinal).append(':');
        if (dimensionType != null) {
            switch (dimensionType) {
                case OVERWORLD:
                    builder.append('w');
                    break;
                case NETHER:
                    builder.append('n');
                    break;
                case THE_END:
                    builder.append('e');
                    break;
            }
        }
        // string with ":" means that the part should be null, instead of unknown.
        // e.g. the dimension part where the phase is not dimensional.
        builder.append(':').append(gamePhase);
        if (subPhase != null) {
            builder.append(':').append(subPhase);
        }
        builder.append(':').append(eventOrdinal);
        builder.append(']');
        return builder.toString();
    }

    @Override
    public int compareTo(@Nonnull TickRecord that) {
        if (this == that) return 0;
        if (tickOrdinal != that.tickOrdinal) return Integer.compare(tickOrdinal, that.tickOrdinal);
        if (gamePhase != that.gamePhase) return gamePhase.compareTo(that.gamePhase);
        if (gamePhase.dimensional && dimensionType != that.dimensionType) {
            return dimensionType.compareTo(that.dimensionType);
        }
        if (subPhase != null && that.subPhase != null) {
            try {
                int spc = subPhase.compareTo(that.subPhase);
                if (spc != 0) return spc;
            } catch (UnsupportedOperationException ignored) {
            }
        }
        return Integer.compare(eventOrdinal, that.eventOrdinal);
    }
}
