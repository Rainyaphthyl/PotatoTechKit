package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkPacketBuffer;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;

import javax.annotation.Nonnull;

public class TileEntitySubPhase extends SubPhase {
    private int ordinal;

    public TileEntitySubPhase(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public GamePhase parentPhase() {
        return GamePhase.TILE_ENTITY_UPDATE;
    }

    @Override
    public int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException {
        if (obj == this) return 0;
        requiresSubClass(obj);
        TileEntitySubPhase that = (TileEntitySubPhase) obj;
        return Integer.compare(getOrdinal(), that.getOrdinal());
    }

    @Override
    public void readFromPacket(@Nonnull ChunkPacketBuffer buffer) {
        ordinal = buffer.readVarInt();
    }

    @Override
    public void writeToPacket(@Nonnull ChunkPacketBuffer buffer) {
        buffer.writeVarInt(getOrdinal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TileEntitySubPhase)) return false;
        TileEntitySubPhase that = (TileEntitySubPhase) o;
        return getOrdinal() == that.getOrdinal();
    }

    @Override
    public int hashCode() {
        return getOrdinal();
    }

    @Override
    public String toString() {
        return String.valueOf(getOrdinal());
    }
}
