package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.world.DimensionType;

public class MutablePhaseClock {
    public static final MutablePhaseClock INSTANCE = new MutablePhaseClock();
    private DimensionType dimension;
    private GamePhase phase;

    private MutablePhaseClock() {
        dimension = null;
        phase = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutablePhaseClock)) return false;
        MutablePhaseClock that = (MutablePhaseClock) o;
        if (dimension != that.dimension) return false;
        return phase == that.phase;
    }

    public PhaseRecord toImmutable() {
        return PhaseRecord.getPooledRecord(dimension, phase);
    }

    public DimensionType getDimension() {
        return dimension;
    }

    public void setDimension(DimensionType dimension) {
        this.dimension = dimension;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void startPhase(GamePhase phase) throws IllegalArgumentException {
        if (this.phase == null) {
            this.phase = phase;
        } else {
            throw new IllegalArgumentException(this.phase + " != " + null);
        }
    }

    public void endPhase(GamePhase phase) throws IllegalArgumentException {
        if (this.phase == phase) {
            this.phase = null;
        } else {
            throw new IllegalArgumentException(this.phase + " != " + phase);
        }
    }
}
