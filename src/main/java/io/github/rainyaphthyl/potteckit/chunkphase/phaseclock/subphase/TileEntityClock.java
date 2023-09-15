package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;

import javax.annotation.Nonnull;

public class TileEntityClock extends MutablePhaseClock.SubPhaseClock {
    private int ordinal;

    public TileEntityClock(MutablePhaseClock parentClock) {
        super(parentClock);
    }

    @Override
    public SubPhase createRecord() {
        return new TileEntitySubPhase(ordinal);
    }

    @Override
    public void reset() {
        ordinal = 0;
    }

    @Override
    protected boolean push() {
        return false;
    }

    @Override
    protected boolean swap() {
        ++ordinal;
        return true;
    }

    @Override
    protected boolean pop() {
        return false;
    }

    @Override
    protected boolean operate(@Nonnull Object... args) {
        return false;
    }
}
