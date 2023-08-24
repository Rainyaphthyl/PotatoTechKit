package io.github.rainyaphthyl.potteckit.server.phaseclock;

public abstract class SubPhase<P extends SubPhase<P>> implements Comparable<P> {
    public final GamePhase parentPhase;

    protected SubPhase(GamePhase parentPhase) {
        this.parentPhase = parentPhase;
    }
}
