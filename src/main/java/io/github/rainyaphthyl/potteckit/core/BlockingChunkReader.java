package io.github.rainyaphthyl.potteckit.core;

import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkProviderServer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockingChunkReader extends SilentChunkReader {
    private static final ConcurrentMap<WorldServer, BlockingChunkReader> instances = new ConcurrentHashMap<>();
    protected final Map<Long, Chunk> chunkCache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true));

    protected BlockingChunkReader(WorldServer world) {
        super(world);
    }

    public static BlockingChunkReader getAccessTo(WorldServer world) {
        return instances.computeIfAbsent(world, BlockingChunkReader::new);
    }

    @Nullable
    @Override
    protected Chunk spectateLoadedChunk(int chunkX, int chunkZ) {
        ChunkProviderServer chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof AccessChunkProviderServer) {
            Chunk chunk = null;
            long index = ChunkPos.asLong(chunkX, chunkZ);
            Long2ObjectMap<Chunk> loadedChunksMap = ((AccessChunkProviderServer) chunkProvider).getLoadedChunksMap();
            if (loadedChunksMap.containsKey(index)) {
                chunk = loadedChunksMap.get(index);
            } else {
                IChunkLoader chunkLoader = ((AccessChunkProviderServer) chunkProvider).getChunkLoader();
                if (chunkLoader.isChunkGeneratedAt(chunkX, chunkZ)) {
                    chunk = chunkCache.get(index);
                    while (chunk == null) {
                        chunk = loadedChunksMap.get(index);
                    }
                }
            }
            if (chunk != null) {
                chunkCache.put(index, chunk);
            }
        }
        return null;
    }
}
