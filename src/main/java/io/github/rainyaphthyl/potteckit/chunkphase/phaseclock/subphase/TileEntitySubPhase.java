package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkPacketBuffer;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;

import javax.annotation.Nonnull;

public class TileEntitySubPhase extends SubPhase {
    @Override
    public GamePhase parentPhase() {
        return null;
    }

    @Override
    public int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public void readFromPacket(@Nonnull ChunkPacketBuffer buffer) {

    }

    @Override
    public void writeToPacket(@Nonnull ChunkPacketBuffer buffer) {

    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
