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
                    StringBuilder builder = new StringBuilder();
                    builder.append("Portal Mapping to ").append(posTarget).append(":\n");
                    for (AxisAlignedBB piece : pieceSources) {
                        builder.append("  ").append(piece).append('\n');
                    }
                    server.getPlayerList().sendMessage(new TextComponentString(builder.toString()));
                    Reference.LOGGER.info(builder.toString());
                }
            } catch (Exception e) {
                Reference.LOGGER.warn(e);
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
        Iterator<BlockPos> iterator = destSet.iterator();
        ThreadGroup threadGroup = new ThreadGroup("Portal Approaches");
        Deque<Thread> threadDeque = new ArrayDeque<>();
        int count = 0;
        while (iterator.hasNext()) {
            threadDeque.clear();
            int permits = 0;
            do {
                BlockPos posDest = iterator.next();
                BlockPos.MutableBlockPos posResult = blockMapPool.computeIfAbsent(posDest, k -> new BlockPos.MutableBlockPos());
                Thread thread = asyncTaskFindClosestTarget(posDest, semaphore, posResult, null, threadGroup);
                thread.setName("Portal Approach " + (count++));
                if (threadDeque.add(thread)) {
                    ++permits;
                }
                if (permits >= maxThreadNum) {
                    break;
                }
            } while (iterator.hasNext());
            if (permits != threadDeque.size()) {
                break;
            }
            for (Thread thread : threadDeque) {
                thread.start();
                server.getPlayerList().sendMessage(new TextComponentString("§7" + thread + " started§r"));
            }
            boolean acquired = false;
            try {
                acquired = semaphore.tryAcquire(permits, 15L * permits, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Reference.LOGGER.warn("{}: {}, {}", e, this, semaphore);
            }
            String message = "§fFinished " + count + " tasks§r";
            server.getPlayerList().sendMessage(new TextComponentString(message));
            if (!acquired) {
                for (Thread thread : threadDeque) {
                    try {
                        thread.interrupt();
                        thread.join();
                    } catch (InterruptedException e) {
                        Reference.LOGGER.warn(e);
                    }
                }
                server.getPlayerList().sendMessage(new TextComponentString("§cSearching failed!§r"));
                break;
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
