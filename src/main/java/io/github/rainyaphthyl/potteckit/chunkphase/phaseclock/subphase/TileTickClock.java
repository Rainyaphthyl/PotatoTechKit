package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessNextTickListEntry;
import net.minecraft.world.NextTickListEntry;

import javax.annotation.Nonnull;

public class TileTickClock extends MutablePhaseClock.SubPhaseClock {
    private int priority;
    private long scheduledTime;
    private long delay;
    private long entryID;
    private long relativeID;
    private long prevStdID;

    public TileTickClock(MutablePhaseClock parentClock) {
        super(parentClock);
    }

    @Override
    public SubPhase createRecord() {
        return new TileTickSubPhase(delay, priority, relativeID);
    }

    @Override
    public void reset() {
        priority = Integer.MIN_VALUE;
        scheduledTime = 0L;
        delay = 0L;
        entryID = 0L;
        relativeID = 0L;
        prevStdID = 0L;
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
        if (args.length < 2) return false;
        if (!(args[0] instanceof Long)) return false;
        long currWorldTime = (Long) args[0];
        if (!(args[1] instanceof NextTickListEntry)) return false;
        NextTickListEntry currEntry = (NextTickListEntry) args[1];
        // delay
        long tempSchedule = currEntry.scheduledTime;
        boolean noChange = tempSchedule == scheduledTime;
        scheduledTime = tempSchedule;
        delay = scheduledTime - currWorldTime;
        // priority
        int tempPriority = currEntry.priority;
        noChange &= tempPriority == priority;
        priority = tempPriority;
        // ID
        long tempID = ((AccessNextTickListEntry) currEntry).getTickEntryID();
        if (!noChange) {
            prevStdID = tempID;
        }
        noChange &= tempID == entryID;
        entryID = tempID;
        relativeID = entryID - prevStdID;
        return !noChange;
    }
}
