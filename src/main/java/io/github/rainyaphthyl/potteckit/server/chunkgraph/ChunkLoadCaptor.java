package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.util.MonoPriorGraph;
import net.minecraft.util.math.ChunkPos;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkLoadCaptor {
    private static final ConcurrentMap<Thread, ChunkLoadSource> threadReasonCache = new ConcurrentHashMap<>();
    private final MonoPriorGraph<ChunkPos, ChunkLoadReason> graph = new MonoPriorGraph<>(ChunkPos.class, ChunkLoadReason.class);

    public static void pushThreadSource(ChunkLoadSource source) {
        Thread thread = Thread.currentThread();
        ChunkLoadSource previous = threadReasonCache.putIfAbsent(thread, source);
        if (previous != null) {
            throw new ConcurrentModificationException("Chunk loader thread " + thread + " is interrupted!");
        }
    }

    /**
     * Fetch and <b>remove</b>
     */
    public static ChunkLoadSource popThreadSource() {
        Thread thread = Thread.currentThread();
        return threadReasonCache.remove(thread);
    }

    public static void removeThreadSource() {
        Thread thread = Thread.currentThread();
        threadReasonCache.remove(thread);
    }
}
