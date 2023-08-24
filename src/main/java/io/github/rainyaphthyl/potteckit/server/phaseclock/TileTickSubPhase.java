package io.github.rainyaphthyl.potteckit.server.phaseclock;

import javax.annotation.Nonnull;

public class TileTickSubPhase extends SubPhase<TileTickSubPhase> {
    public TileTickSubPhase() {
        super(GamePhase.TILE_TICK);
    }

    @Override
    public int compareTo(@Nonnull TileTickSubPhase o) {
        return 0;
    }
}
