package io.github.rainyaphthyl.potteckit.core;

import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkProviderServer;
import io.github.rainyaphthyl.potteckit.util.Reference;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilentChunkReader extends ChunkReader {
    private static final ConcurrentMap<WorldServer, SilentChunkReader> instances = new ConcurrentHashMap<>();
    protected final WorldServer worldServer;
    protected final ConcurrentMap<Long, Chunk> chunkCache = new ConcurrentHashMap<>();

    protected SilentChunkReader(WorldServer worldServer) {
        super(worldServer);
        this.worldServer = worldServer;
    }

    public static SilentChunkReader getAccessTo(WorldServer worldServer) {
        return instances.computeIfAbsent(worldServer, SilentChunkReader::new);
    }

    @Override
    @Nullable
    public Chunk spectateLoadedChunk(int chunkX, int chunkZ, boolean blocking) throws InterruptedException {
        ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
        if (chunkProvider instanceof AccessChunkProviderServer) {
            Chunk chunk;
            long index = ChunkPos.asLong(chunkX, chunkZ);
            Long2ObjectMap<Chunk> loadedChunksMap = ((AccessChunkProviderServer) chunkProvider).getLoadedChunksMap();
            if (loadedChunksMap.containsKey(index)) {
                chunk = loadedChunksMap.get(index);
            } else {
                chunk = chunkCache.get(index);
                if (blocking && chunk == null) {
                    Semaphore semaphore = new Semaphore(0);
                    AtomicReference<Chunk> chunkRef = new AtomicReference<>(null);
                    AtomicBoolean loading = new AtomicBoolean(true);
                    Thread thread = new Thread(() -> {
                        MinecraftServer server = worldServer.getMinecraftServer();
                        if (server != null) {
                            Chunk temp;
                            do {
                                temp = loadedChunksMap.get(index);
                            } while (temp == null && loading.get());
                            chunkRef.set(temp);
                            Reference.LOGGER.warn("Finished: {}", this);
                        }
                        semaphore.release();
                    }, "Blocking Chunk Loader");
                    thread.setDaemon(true);
                    thread.setPriority(1);
                    thread.start();
                    InterruptedException thrown = null;
                    try {
                        semaphore.acquire();
                        chunk = chunkRef.get();
                    } catch (InterruptedException e) {
                        Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
                        thrown = e;
                    } finally {
                        loading.set(false);
                        Reference.LOGGER.warn("Waiting for {} to die...", thread);
                        thread.join();
                    }
                    if (thrown != null) {
                        throw thrown;
                    }
                }
            }
            if (chunk != null) {
                chunkCache.put(index, chunk);
            }
            return chunk;
        }
        return null;
    }
}
