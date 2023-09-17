package io.github.rainyaphthyl.potteckit.chunkphase.loader;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkProviderClient;
import io.github.rainyaphthyl.potteckit.util.NetworkGraph;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkLoaderChecker {
    private static final Lock LOCK = new ReentrantLock();

    /**
     * Client-side
     */
    public static void clientCheckChunkLoader() {
        Runnable runnable = () -> {
            if (!LOCK.tryLock()) {
                return;
            }
            try {
                Minecraft minecraft = Minecraft.getMinecraft();
                WorldClient world = minecraft.world;
                ChunkProviderClient chunkProvider = world.getChunkProvider();
                if (chunkProvider instanceof AccessChunkProviderClient) {
                    Long2ObjectMap<Chunk> loadedChunks = ((AccessChunkProviderClient) chunkProvider).getLoadedChunks();
                    NetworkGraph<ChunkPos, BlockPos> graph = new NetworkGraph<>(ChunkPos.class, BlockPos.class);
                    for (Long2ObjectMap.Entry<Chunk> chunkEntry : loadedChunks.long2ObjectEntrySet()) {
                        Chunk chunk = chunkEntry.getValue();
                        Map<BlockPos, TileEntity> tileEntityMap = chunk.getTileEntityMap();
                        for (Map.Entry<BlockPos, TileEntity> btEntry : tileEntityMap.entrySet()) {
                            TileEntity tileEntity = btEntry.getValue();
                            if (tileEntity instanceof TileEntityHopper) {
                                TileEntityHopper hopper = (TileEntityHopper) tileEntity;
                                BlockPos blockPos = btEntry.getKey();
                                IBlockState blockState = chunk.getBlockState(blockPos);
                                Block block = blockState.getBlock();
                                if (block instanceof BlockHopper) {
                                    int meta = block.getMetaFromState(blockState);
                                    boolean enabled = BlockHopper.isEnabled(meta);
                                    boolean notEmpty = world.isRemote || !hopper.isEmpty();
                                    if (enabled && notEmpty) {
                                        EnumFacing enumfacing = BlockHopper.getFacing(meta);
                                        BlockPos nextPos = blockPos.offset(enumfacing);
                                        ChunkPos target = new ChunkPos(nextPos);
                                        ChunkPos source = chunk.getPos();
                                        if (!target.equals(source)) {
                                            graph.addEdge(target, source, blockPos);
                                            String message = source + " -> " + target;
                                            MessageDispatcher.generic().type(MessageOutput.CHAT).send(message);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                LOCK.unlock();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("Chunk Loader Checker " + thread.getId());
        thread.start();
    }
}
