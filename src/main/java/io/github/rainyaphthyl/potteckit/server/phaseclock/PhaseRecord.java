package io.github.rainyaphthyl.potteckit.server.phaseclock;

import io.github.rainyaphthyl.potteckit.util.NullComparator;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class PhaseRecord implements Comparable<PhaseRecord> {
    public static final Comparator<GamePhase> PHASE_COMPARATOR = new NullComparator<>();
    public static final Comparator<DimensionType> DIMENSION_COMPARATOR = new NullComparator<>();
    private static final Map<DimensionType, Map<GamePhase, PhaseRecord>> worldRecordPool
            = Collections.synchronizedMap(new EnumMap<>(DimensionType.class));
    private static final Map<GamePhase, PhaseRecord> globalRecordPool
            = Collections.synchronizedMap(new EnumMap<>(GamePhase.class));
    public final DimensionType dimension;
    public final GamePhase phase;

    private PhaseRecord(DimensionType dimension, GamePhase phase) {
        this.dimension = dimension;
        this.phase = phase;
    }

    public static PhaseRecord getPooledRecord(@Nullable DimensionType dimension, GamePhase phase) {
        if (phase == null) {
            return null;
        } else if (dimension == null || !phase.dimensional) {
            return getPooledRecord(phase);
        } else {
            Map<GamePhase, PhaseRecord> partialPool = worldRecordPool.computeIfAbsent(dimension,
                    key -> Collections.synchronizedMap(new EnumMap<>(GamePhase.class))
            );
            return partialPool.computeIfAbsent(phase, key -> new PhaseRecord(dimension, key));
        }
    }

    public static PhaseRecord getPooledRecord(GamePhase phase) {
        if (phase == null || phase.dimensional) {
            return null;
        } else {
            return globalRecordPool.computeIfAbsent(phase, key -> new PhaseRecord(null, key));
        }
    }

    @Override
    public int compareTo(@Nonnull PhaseRecord that) {
        if (this == that) return 0;
        if (phase == that.phase) {
            if (GamePhase.isNullOrDimensional(phase)) {
                return DIMENSION_COMPARATOR.compare(dimension, that.dimension);
            } else {
                return 0;
            }
        } else {
            if (GamePhase.isNullOrDimensional(phase) && GamePhase.isNullOrDimensional(that.phase)) {
                if (dimension == that.dimension) {
                    return PHASE_COMPARATOR.compare(phase, that.phase);
                } else {
                    return DIMENSION_COMPARATOR.compare(dimension, that.dimension);
                }
            } else {
                return PHASE_COMPARATOR.compare(phase, that.phase);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhaseRecord)) return false;
        PhaseRecord that = (PhaseRecord) o;
        if (dimension != that.dimension) return false;
        return phase == that.phase;
    }

    @Override
    public int hashCode() {
        int result = dimension != null ? dimension.hashCode() : 0;
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" + dimension + ", " + phase + '}';
    }
}
