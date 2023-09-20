package io.github.rainyaphthyl.potteckit.core;

import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkProviderServer;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessWorld;
import io.github.rainyaphthyl.potteckit.util.Reference;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilentChunkReader implements IBlockAccess {
    private static final ConcurrentMap<WorldServer, SilentChunkReader> instances = new ConcurrentHashMap<>();
    protected final WorldServer world;
    protected final Map<Long, Chunk> chunkCache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true));

    protected SilentChunkReader(WorldServer world) {
        this.world = world;
    }

    public static SilentChunkReader getAccessTo(WorldServer world) {
        return instances.computeIfAbsent(world, SilentChunkReader::new);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        boolean outOfRange = true;
        if (world instanceof AccessWorld) {
            outOfRange = ((AccessWorld) world).invokeIsOutsideBuildHeight(pos);
        }
        if (!outOfRange) {
            Chunk chunk = spectateLoadedChunkImmediate(pos);
            if (chunk != null) {
                Map<BlockPos, TileEntity> tileEntityMap = chunk.getTileEntityMap();
                return tileEntityMap.get(pos);
            }
        }
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        boolean outOfRange = true;
        if (world instanceof AccessWorld) {
            outOfRange = ((AccessWorld) world).invokeIsOutsideBuildHeight(pos);
        }
        if (!outOfRange) {
            Chunk chunk = spectateLoadedChunkImmediate(pos);
            if (chunk != null) {
                return chunk.getBlockState(pos);
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return getBlockState(pos).getMaterial() == Material.AIR;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        BiomeProvider biomeProvider = world.getBiomeProvider();
        if (world.isBlockLoaded(pos)) {
            Chunk chunk = spectateLoadedChunkImmediate(pos);
            if (chunk != null) {
                return chunk.getBiome(pos, biomeProvider);
            }
        }
        return biomeProvider.getBiome(pos, Biomes.PLAINS);

    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return world.getWorldType();
    }

    /**
     * Do not modify the {@code unloadQueued} flag
     */
    @Nullable
    public Chunk spectateLoadedChunkImmediate(int chunkX, int chunkZ) {
        try {
            return spectateLoadedChunk(chunkX, chunkZ, false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Do not modify the {@code unloadQueued} flag
     */
    @Nullable
    public Chunk spectateLoadedChunkImmediate(BlockPos blockPos) {
        return spectateLoadedChunkImmediate(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Nullable
    public Chunk spectateLoadedChunk(BlockPos blockPos, boolean blocking) throws InterruptedException {
        return spectateLoadedChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, blocking);
    }

    @Nullable
    public Chunk spectateLoadedChunk(int chunkX, int chunkZ, boolean blocking) throws InterruptedException {
        ChunkProviderServer chunkProvider = world.getChunkProvider();
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
                        MinecraftServer server = world.getMinecraftServer();
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
