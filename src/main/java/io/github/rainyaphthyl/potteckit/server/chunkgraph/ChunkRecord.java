package io.github.rainyaphthyl.potteckit.server.chunkgraph;

public class ChunkRecord {
    public enum Event {
        LOADING,
        QUEUE_UNLOAD,
        CANCEL_UNLOAD,
        UNLOADING,
    }
}
