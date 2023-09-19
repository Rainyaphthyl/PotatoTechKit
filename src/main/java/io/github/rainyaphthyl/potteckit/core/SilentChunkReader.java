package io.github.rainyaphthyl.potteckit.core;

import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkProviderServer;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessWorld;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilentChunkReader implements IBlockAccess {
    private static final ConcurrentMap<WorldServer, SilentChunkReader> instances = new ConcurrentHashMap<>();
    protected final WorldServer world;

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
            Chunk chunk = spectateLoadedChunk(pos);
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
            Chunk chunk = spectateLoadedChunk(pos);
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
            Chunk chunk = spectateLoadedChunk(pos);
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
    protected Chunk spectateLoadedChunk(int chunkX, int chunkZ) {
        ChunkProviderServer chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof AccessChunkProviderServer) {
            Chunk chunk = null;
            long index = ChunkPos.asLong(chunkX, chunkZ);
            Long2ObjectMap<Chunk> loadedChunksMap = ((AccessChunkProviderServer) chunkProvider).getLoadedChunksMap();
            if (loadedChunksMap.containsKey(index)) {
                chunk = loadedChunksMap.get(index);
            }
            return chunk;
        }
        return null;
    }

    /**
     * Do not modify the {@code unloadQueued} flag
     */
    @Nullable
    protected Chunk spectateLoadedChunk(BlockPos blockPos) {
        return spectateLoadedChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }
}
