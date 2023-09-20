package io.github.rainyaphthyl.potteckit.core.portal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PortalSearcherRangeForward extends PortalSearcher {
    private final AxisAlignedBB boxSource;
    private BlockPos posDestMin;
    private BlockPos posDestMax;

    public PortalSearcherRangeForward(MinecraftServer server, DimensionType dimSource, AxisAlignedBB boxSource) {
        super(server, dimSource);
        this.boxSource = Objects.requireNonNull(boxSource);
    }

    @Override
    public void run() {
        if (lock.tryLock()) {
            try {
                initialize();
                Map<BlockPos, BlockPos.MutableBlockPos> blockMapPool = findTargetFromDestOrigins();
                Multimap<BlockPos, AxisAlignedBB> portalPieceInvMap = findTargetsFromParts(blockMapPool);
                for (Map.Entry<BlockPos, Collection<AxisAlignedBB>> entry : portalPieceInvMap.asMap().entrySet()) {
                    BlockPos posTarget = entry.getKey();
                    Collection<AxisAlignedBB> pieceSources = entry.getValue();
                    SortedSet<AxisAlignedBB> pieceList = new TreeSet<>(pieceSources);
                    StringBuilder builder = new StringBuilder();
                    builder.append("Portal Mapping to ").append(posTarget).append(":\n");
                    for (AxisAlignedBB piece : pieceList) {
                        builder.append("  ").append(piece).append('\n');
                    }
                    server.getPlayerList().sendMessage(new TextComponentString(builder.toString()));
                    Reference.LOGGER.info(builder.toString());
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Nonnull
    private Multimap<BlockPos, AxisAlignedBB> findTargetsFromParts(@Nonnull Map<BlockPos, BlockPos.MutableBlockPos> blockMapPool) {
        Multimap<BlockPos, AxisAlignedBB> portalPieceInvMap = HashMultimap.create();
        for (Map.Entry<BlockPos, BlockPos.MutableBlockPos> entry : blockMapPool.entrySet()) {
            BlockPos posDestOrigin = entry.getKey().toImmutable();
            BlockPos posTarget = entry.getValue().toImmutable();
            AxisAlignedBB boxSourcePiece = getMappingAreaTo(posDestOrigin).intersect(boxSource);
            portalPieceInvMap.put(posTarget, boxSourcePiece);
        }
        return portalPieceInvMap;
    }

    @Nonnull
    private Map<BlockPos, BlockPos.MutableBlockPos> findTargetFromDestOrigins() {
        Iterable<BlockPos> destSet = BlockPos.getAllInBox(posDestMin, posDestMax);
        Semaphore semaphore = new Semaphore(0);
        Map<BlockPos, BlockPos.MutableBlockPos> blockMapPool = new HashMap<>();
        int permits = 0;
        ThreadGroup threadGroup = new ThreadGroup("Portal Approaches");
        for (BlockPos posDest : destSet) {
            BlockPos.MutableBlockPos posResult = blockMapPool.computeIfAbsent(posDest, k -> new BlockPos.MutableBlockPos());
            Thread thread = asyncFindClosestTarget(posDest, semaphore, posResult, null, threadGroup);
            if (thread != null) {
                ++permits;
            }
        }
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(permits, 45L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Reference.LOGGER.warn("{}: {}, {}", e, this, semaphore);
        }
        if (!acquired) {
            threadGroup.interrupt();
            Thread[] threadList = new Thread[permits];
            threadGroup.enumerate(threadList);
            for (Thread thread : threadList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Reference.LOGGER.warn(e);
                }
            }
        }
        return blockMapPool;
    }

    @Override
    protected void initialize() {
        initSetWorld();
        posDestMin = getMappingBlockFrom(boxSource.minX, boxSource.minY, boxSource.minZ, false);
        posDestMax = getMappingBlockFrom(boxSource.maxX, boxSource.maxY, boxSource.maxZ, true);
    }
}
