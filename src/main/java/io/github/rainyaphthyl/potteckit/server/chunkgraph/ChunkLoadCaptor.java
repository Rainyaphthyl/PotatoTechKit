package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.util.MonoPriorGraph;
import net.minecraft.util.math.ChunkPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkLoadCaptor {
    private final ConcurrentMap<Thread, ChunkLoadReason> threadReasonCache = new ConcurrentHashMap<>();
    private final MonoPriorGraph<ChunkPos, ChunkLoadReason> graph = new MonoPriorGraph<>(ChunkPos.class, ChunkLoadReason.class);

    public void test() {
    }
}
