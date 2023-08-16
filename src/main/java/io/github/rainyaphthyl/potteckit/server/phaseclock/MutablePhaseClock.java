package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.world.DimensionType;

public class MutablePhaseClock {
    private DimensionType dimension;
    private GamePhase phase;

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

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }
}
