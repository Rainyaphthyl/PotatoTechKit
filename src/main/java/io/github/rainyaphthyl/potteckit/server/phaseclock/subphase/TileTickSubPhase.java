package io.github.rainyaphthyl.potteckit.server.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;

import javax.annotation.Nonnull;

public class TileTickSubPhase extends SubPhase {
    @Override
    public GamePhase parentPhase() {
        return GamePhase.TILE_TICK;
    }

    @Override
    public int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
