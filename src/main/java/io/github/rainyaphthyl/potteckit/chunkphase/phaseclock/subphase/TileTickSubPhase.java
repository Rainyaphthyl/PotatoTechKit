package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkPacketBuffer;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;

import javax.annotation.Nonnull;

public class TileTickSubPhase extends SubPhase {
    /**
     * normally as {@code 0}; in case the TT executes earlier or later than schedule
     */
    private long delay;
    private int priority;
    private long relativeID;

    public TileTickSubPhase(long delay, int priority, long relativeID) {
        this.delay = delay;
        this.priority = priority;
        this.relativeID = relativeID;
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
        } else if (getRelativeID() < that.getRelativeID()) {
            return -1;
        } else {
            return getRelativeID() > that.getRelativeID() ? 1 : 0;
        }
    }

    @Override
    public void readFromPacket(@Nonnull ChunkPacketBuffer buffer) {
        delay = buffer.readVarLong();
        priority = buffer.readSignedVarInt();
        relativeID = buffer.readVarLong();
    }

    @Override
    public void writeToPacket(@Nonnull ChunkPacketBuffer buffer) {
        buffer.writeVarLong(getDelay());
        buffer.writeSignedVarInt(getPriority());
        buffer.writeVarLong(getRelativeID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TileTickSubPhase)) return false;
        TileTickSubPhase that = (TileTickSubPhase) o;
        if (getDelay() != that.getDelay()) return false;
        if (getPriority() != that.getPriority()) return false;
        return getRelativeID() == that.getRelativeID();
    }

    @Override
    public int hashCode() {
        int result = (int) (getDelay() ^ (getDelay() >>> 32));
        result = 31 * result + getPriority();
        result = 31 * result + (int) (getRelativeID() ^ (getRelativeID() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(getDelay()) + ':' + getPriority() + ':' + getRelativeID();
    }

    public long getDelay() {
        return delay;
    }

    public int getPriority() {
        return priority;
    }

    public long getRelativeID() {
        return relativeID;
    }
}
