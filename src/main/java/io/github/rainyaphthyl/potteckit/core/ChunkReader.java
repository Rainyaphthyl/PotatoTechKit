package io.github.rainyaphthyl.potteckit.core;

import io.github.rainyaphthyl.potteckit.mixin.access.AccessWorld;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ChunkReader implements IBlockAccess {
    protected final World world;

    protected ChunkReader(World world) {
        this.world = world;
    }

    @Nullable
    public static ChunkReader getAccessTo(World world) {
        if (world instanceof WorldServer) {
            return SilentChunkReader.getAccessTo((WorldServer) world);
        } else if (world instanceof WorldClient) {
            return ClientChunkReader.getAccessTo((WorldClient) world);
        } else {
            return null;
        }
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
    public abstract Chunk spectateLoadedChunk(int chunkX, int chunkZ, boolean blocking) throws InterruptedException;
}
