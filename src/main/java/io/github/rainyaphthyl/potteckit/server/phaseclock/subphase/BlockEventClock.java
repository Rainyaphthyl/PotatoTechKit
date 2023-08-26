package io.github.rainyaphthyl.potteckit.server.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;

public class BlockEventClock extends MutablePhaseClock.SubPhaseClock {
    private int depth = 0;
    private int orderAtDepth = 0;

    public BlockEventClock(MutablePhaseClock parentClock) {
        super(parentClock);
    }

    @Override
    public SubPhase createRecord() {
        return new BlockEventSubPhase(depth, orderAtDepth);
    }

    @Override
    public void reset() {
        depth = 0;
        orderAtDepth = 0;
    }

    /**
     * @return {@code true} if successful
     */
    @Override
    protected boolean push() {
        ++depth;
        orderAtDepth = 0;
        return true;
    }

    /**
     * @return {@code true} if successful
     */
    @Override
    protected boolean swap() {
        ++orderAtDepth;
        return true;
    }

    /**
     * @return {@code true} if successful
     */
    @Override
    protected boolean pop() {
        --depth;
        orderAtDepth = 0;
        return true;
    }
}
