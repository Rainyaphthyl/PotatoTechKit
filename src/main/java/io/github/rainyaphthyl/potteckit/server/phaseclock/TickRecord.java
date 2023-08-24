package io.github.rainyaphthyl.potteckit.server.phaseclock;

import io.github.rainyaphthyl.potteckit.server.phaseclock.subphase.SubPhase;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A timestamp containing gametick, dimension, phase, and other arguments.
 */
public class TickRecord implements Comparable<TickRecord> {
    /**
     * The global and <b>comparable</b> tick counter (int), increasing <b>between</b> ticks.
     */
    public final long tickOrdinal;
    /**
     * The dimensional game time output from Command Blocks, increasing <b>during</b> each tick.
     */
    public final long gameTime;
    public final DimensionType dimensionType;
    public final GamePhase gamePhase;
    /**
     * Sub-phases and the unique sub-tick ordinal.
     */
    public final SubPhase subPhase;

    public TickRecord(long tickOrdinal, long gameTime, DimensionType dimensionType, GamePhase gamePhase, SubPhase subPhase) throws NullPointerException, IllegalArgumentException {
        this.tickOrdinal = tickOrdinal;
        this.gameTime = gameTime;
        this.gamePhase = Objects.requireNonNull(gamePhase);
        if (subPhase != null && subPhase.parentPhase() != gamePhase) {
            throw new IllegalArgumentException();
        }
        this.subPhase = subPhase;
        this.dimensionType = gamePhase.dimensional ? Objects.requireNonNull(dimensionType) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TickRecord)) return false;
        TickRecord that = (TickRecord) obj;
        if (tickOrdinal != that.tickOrdinal) return false;
        if (dimensionType != that.dimensionType) return false;
        if (gamePhase != that.gamePhase) return false;
        return Objects.equals(subPhase, that.subPhase);
    }

    @Override
    public int hashCode() {
        int result = (int) (tickOrdinal ^ (tickOrdinal >>> 32));
        result = 31 * result + (int) (gameTime ^ (gameTime >>> 32));
        result = 31 * result + (dimensionType != null ? dimensionType.hashCode() : 0);
        result = 31 * result + gamePhase.hashCode();
        result = 31 * result + (subPhase != null ? subPhase.hashCode() : 0);
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
        builder.append(']');
        return builder.toString();
    }

    @Override
    public int compareTo(@Nonnull TickRecord that) {
        if (this == that) return 0;
        if (tickOrdinal != that.tickOrdinal) return Long.compare(tickOrdinal, that.tickOrdinal);
        if (gamePhase != that.gamePhase) return gamePhase.compareTo(that.gamePhase);
        if (gamePhase.dimensional && dimensionType != that.dimensionType) {
            return dimensionType.compareTo(that.dimensionType);
        }
        if (subPhase != null && that.subPhase != null) {
            return subPhase.compareTo(that.subPhase);
        } else {
            return 0;
        }
    }
}
