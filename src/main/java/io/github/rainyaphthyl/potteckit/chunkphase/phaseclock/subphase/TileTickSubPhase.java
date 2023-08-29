package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkPacketBuffer;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;

import javax.annotation.Nonnull;

public class TileTickSubPhase extends SubPhase {
    private long delay;
    private int priority;
    private long entryID;

    public TileTickSubPhase(long delay, int priority, long entryID) {
        this.delay = delay;
        this.priority = priority;
        this.entryID = entryID;
    }

    @Override
    public GamePhase parentPhase() {
        return GamePhase.TILE_TICK;
    }

    @Override
    public int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException {
        if (obj == this) return 0;
        requiresSubClass(obj);
        TileTickSubPhase that = (TileTickSubPhase) obj;
        if (getDelay() < that.getDelay()) {
            return -1;
        } else if (getDelay() > that.getDelay()) {
            return 1;
        } else if (getPriority() != that.getPriority()) {
            return getPriority() - that.getPriority();
        } else if (getEntryID() < that.getEntryID()) {
            return -1;
        } else {
            return getEntryID() > that.getEntryID() ? 1 : 0;
        }
    }

    @Override
    public void readFromPacket(@Nonnull ChunkPacketBuffer buffer) {
        delay = buffer.readVarLong();
        priority = buffer.readSignedVarInt();
        entryID = buffer.readVarLong();
    }

    @Override
    public void writeToPacket(@Nonnull ChunkPacketBuffer buffer) {
        buffer.writeVarLong(getDelay());
        buffer.writeSignedVarInt(getPriority());
        buffer.writeVarLong(getEntryID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TileTickSubPhase)) return false;
        TileTickSubPhase that = (TileTickSubPhase) o;
        if (getDelay() != that.getDelay()) return false;
        if (getPriority() != that.getPriority()) return false;
        return getEntryID() == that.getEntryID();
    }

    @Override
    public int hashCode() {
        int result = (int) (getDelay() ^ (getDelay() >>> 32));
        result = 31 * result + getPriority();
        result = 31 * result + (int) (getEntryID() ^ (getEntryID() >>> 32));
        return result;
    }

    public long getDelay() {
        return delay;
    }

    public int getPriority() {
        return priority;
    }

    public long getEntryID() {
        return entryID;
    }
}
