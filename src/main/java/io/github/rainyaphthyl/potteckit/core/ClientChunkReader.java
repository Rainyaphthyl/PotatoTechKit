package io.github.rainyaphthyl.potteckit.core;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientChunkReader extends ChunkReader {
    private static final ConcurrentMap<WorldClient, ClientChunkReader> instances = new ConcurrentHashMap<>();
    protected final WorldClient worldClient;
    protected final Map<Long, Chunk> chunkCache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true));

    protected ClientChunkReader(WorldClient worldClient) {
        super(worldClient);
        this.worldClient = worldClient;
    }

    public static ClientChunkReader getAccessTo(WorldClient worldClient) {
        return instances.computeIfAbsent(worldClient, ClientChunkReader::new);
    }

    @Nullable
    @Override
    public Chunk spectateLoadedChunk(int chunkX, int chunkZ, boolean blocking) throws InterruptedException {
        return null;
    }
}
