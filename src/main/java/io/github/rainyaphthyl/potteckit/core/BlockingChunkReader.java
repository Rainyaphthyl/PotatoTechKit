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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            Chunk chunk;
            long index = ChunkPos.asLong(chunkX, chunkZ);
            Long2ObjectMap<Chunk> loadedChunksMap = ((AccessChunkProviderServer) chunkProvider).getLoadedChunksMap();
            if (loadedChunksMap.containsKey(index)) {
                chunk = loadedChunksMap.get(index);
            } else {
                chunk = chunkCache.get(index);
                if (chunk == null) {
                    Lock lock = new ReentrantLock();
                    Condition condition = lock.newCondition();
                    AtomicReference<Chunk> chunkRef = new AtomicReference<>(null);
                    Thread thread = new Thread(() -> {
                        try {
                            lock.lockInterruptibly();
                            MinecraftServer server = world.getMinecraftServer();
                            if (server != null) {
                                Chunk temp;
                                do {
                                    temp = loadedChunksMap.get(index);
                                } while (temp == null);
                                chunkRef.set(temp);
                            }
                        } catch (InterruptedException e) {
                            Reference.LOGGER.warn("Interrupted: {}", this);
                        } finally {
                            condition.signalAll();
                            lock.unlock();
                        }
                    }, "Blocking Chunk Loader");
                    thread.setDaemon(true);
                    thread.setPriority(1);
                    thread.start();
                    try {
                        lock.lockInterruptibly();
                        condition.await();
                        chunk = chunkRef.get();
                    } catch (InterruptedException ignored) {
                        Reference.LOGGER.warn("Interrupted: {}", this);
                        thread.interrupt();
                    } finally {
                        lock.unlock();
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
