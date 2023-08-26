package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

public enum ChunkLoadReason {
    HOPPER_POINTING("Hopper Pointing Out");
    private final String description;

    ChunkLoadReason(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
