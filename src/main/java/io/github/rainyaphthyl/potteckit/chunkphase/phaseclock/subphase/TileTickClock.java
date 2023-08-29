package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessNextTickListEntry;
import net.minecraft.world.NextTickListEntry;

import javax.annotation.Nonnull;

public class TileTickClock extends MutablePhaseClock.SubPhaseClock {
    private int priority;
    private long delay;
    private long entryID;

    public TileTickClock(MutablePhaseClock parentClock) {
        super(parentClock);
    }

    @Override
    public SubPhase createRecord() {
        return new TileTickSubPhase(delay, priority, entryID);
    }

    @Override
    public void reset() {
        priority = Integer.MIN_VALUE;
        delay = 0L;
        entryID = 0L;
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
        if (args.length < 1) return false;
        Object object = args[0];
        if (!(object instanceof NextTickListEntry)) return false;
        NextTickListEntry currEntry = (NextTickListEntry) object;
        // delay
        long tempDelay = currEntry.scheduledTime;
        boolean flag = tempDelay == delay;
        delay = tempDelay;
        // priority
        int tempPriority = currEntry.priority;
        flag &= tempPriority == priority;
        priority = tempPriority;
        // ID
        long tempID = ((AccessNextTickListEntry) currEntry).getTickEntryID();
        flag &= tempID == entryID;
        entryID = tempID;
        return !flag;
    }
}
