package io.github.rainyaphthyl.potteckit.server.chunkgraph;

public enum ChunkEvent {
    GENERATING("Generating", false),
    LOADING("Loading", false),
    CANCEL_UNLOAD("Cancel Unload", true),
    QUEUE_UNLOAD("Queue Unload", true),
    UNLOADING("Unloading", false);
    public final String name;
    public final boolean silent;

    ChunkEvent(String name, boolean silent) {
        this.name = name;
        this.silent = silent;
    }
}
