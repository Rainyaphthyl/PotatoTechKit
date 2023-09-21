package io.github.rainyaphthyl.potteckit.core;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
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
import java.util.Objects;
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
    private final MinecraftServer server;

    protected SilentChunkReader(WorldServer worldServer) {
        super(worldServer);
        this.worldServer = worldServer;
        server = worldServer.getMinecraftServer();
        Objects.requireNonNull(server);
    }

    public static SilentChunkReader getAccessTo(WorldServer worldServer) {
        return instances.computeIfAbsent(worldServer, SilentChunkReader::new);
    }

    @Override
    @Nullable
    public Chunk spectateLoadedChunk(int chunkX, int chunkZ, boolean blocking) throws InterruptedException {
        long index = ChunkPos.asLong(chunkX, chunkZ);
        boolean isNew = false;
        Chunk chunk = queuedGetChunk(index);
        if (chunk == null) {
            chunk = chunkCache.get(index);
            if (blocking && chunk == null) {
                Semaphore semaphore = new Semaphore(0);
                AtomicReference<Chunk> chunkRef = new AtomicReference<>(null);
                AtomicBoolean loading = new AtomicBoolean(true);
                Thread thread = new Thread(() -> {
                    Chunk temp;
                    do {
                        temp = queuedGetChunk(index);
                    } while (temp == null && loading.get());
                    chunkRef.set(temp);
                    Reference.LOGGER.warn("Finished: {}", this);
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
                if (chunk != null) {
                    isNew = true;
                }
            }
        } else {
            isNew = true;
        }
        if (isNew) {
            chunkCache.put(index, chunk);
        }
        return chunk;
    }

    @Nullable
    private Chunk queuedGetChunk(long index) {
        AtomicReference<Chunk> chunkPool = new AtomicReference<>(null);
        ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
        if (chunkProvider instanceof AccessChunkProviderServer) {
            Long2ObjectMap<Chunk> loadedChunksMap = ((AccessChunkProviderServer) chunkProvider).getLoadedChunksMap();
            try {
                if (loadedChunksMap.containsKey(index)) {
                    Chunk chunk = loadedChunksMap.get(index);
                    if (chunk != null) {
                        MessageOutput.VANILLA_HOTBAR.send("Getting loaded chunk " + chunk.getPos(), MessageDispatcher.generic());
                    }
                    return chunk;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Reference.LOGGER.warn(e);
                return null;
            }
        }
        return chunkPool.get();
    }
}
