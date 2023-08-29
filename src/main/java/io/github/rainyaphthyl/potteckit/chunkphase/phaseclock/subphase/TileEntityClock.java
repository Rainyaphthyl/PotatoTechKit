package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;

import javax.annotation.Nonnull;

public class TileEntityClock extends MutablePhaseClock.SubPhaseClock {
    public TileEntityClock(MutablePhaseClock parentClock) {
        super(parentClock);
    }

    @Override
    public SubPhase createRecord() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    protected boolean push() {
        return false;
    }

    @Override
    protected boolean swap() {
        return false;
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
