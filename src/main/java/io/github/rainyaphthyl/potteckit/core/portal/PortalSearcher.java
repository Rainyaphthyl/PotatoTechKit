package io.github.rainyaphthyl.potteckit.core.portal;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.core.ChunkReader;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PortalSearcher implements Runnable {
    public static final double INTER_DIM_RATE = 8.0;
    public static final double BORDER_WIDTH = 16.0;
    public static final int BORDER_POS = 29999872;
    protected static final Lock LOCK = new ReentrantLock();
    protected final int maxThreadNum;
    protected final MinecraftServer server;
    protected final DimensionType dimSource;
    protected final ConcurrentMap<BlockPos, Tuple<Double, List<BlockPos>>> targetCache = new ConcurrentHashMap<>();
    protected WorldServer worldDest = null;

    public PortalSearcher(MinecraftServer server, DimensionType dimSource) {
        this.server = Objects.requireNonNull(server);
        this.dimSource = Objects.requireNonNull(dimSource);
        //int i = Math.max(1, (int) ((double) Runtime.getRuntime().maxMemory() * 0.3D) / 10485760);
        maxThreadNum = Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    @Override
    public abstract void run();

    /**
     * Simulates vanilla searching
     *
     * @return the destination block and the respective minimal distance squared
     */
    protected Tuple<BlockPos, Double> findClosestDestination(BlockPos posDestOrigin) {
        Semaphore semaphore = new Semaphore(0);
        BlockPos.MutableBlockPos posResult = new BlockPos.MutableBlockPos();
        AtomicDouble distSqMin = new AtomicDouble(-1.0);
        Thread thread = asyncTaskFindClosestTarget(posDestOrigin, semaphore, posResult, distSqMin, null);
        thread.start();
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(45L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
        }
        if (!acquired) {
            posResult.setPos(BlockPos.ORIGIN);
            distSqMin.set(-1.0);
            thread.interrupt();
            server.getPlayerList().sendMessage(new TextComponentString("§cFailed to check nearest portal...§r"), true);
            try {
                thread.join();
            } catch (InterruptedException e) {
                Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
            } finally {
                server.getPlayerList().sendMessage(new TextComponentString("§cStopped portal checking§r"), true);
            }
        }
        return new Tuple<>(posResult.toImmutable(), distSqMin.get());
    }

    @ParametersAreNonnullByDefault
    protected Thread asyncTaskFindClosestTarget(BlockPos posDestOrigin, Semaphore semaphore, @Nullable BlockPos.MutableBlockPos posResultPool, @Nullable AtomicDouble distSqMinPool, @Nullable ThreadGroup threadGroup) {
        BlockPos posOrigin = posDestOrigin.toImmutable();
        Runnable task = () -> {
            try {
                ChunkReader reader = ChunkReader.getAccessTo(worldDest);
                BlockPos.MutableBlockPos posResult = new BlockPos.MutableBlockPos();
                double distSqMin = -1.0;
                if (reader == null) {
                    return;
                }
                int actualLimit = worldDest.getActualHeight() - 1;
                BlockPos.MutableBlockPos posPortal = new BlockPos.MutableBlockPos();
                for (int bx = -128; bx <= 128; ++bx) {
                    int xDetect = posOrigin.getX() + bx;
                    for (int bz = -128; bz <= 128; ++bz) {
                        int zDetect = posOrigin.getZ() + bz;
                        posPortal.setPos(xDetect, 0, zDetect);
                        Chunk chunk = reader.spectateLoadedChunk(posPortal, true);
                        if (chunk == null) {
                            throw new InterruptedException("Empty chunk time out!");
                        }
                        for (int yDetect = actualLimit; yDetect >= 0; --yDetect) {
                            posPortal.setY(yDetect);
                            IBlockState stateToDetect = chunk.getBlockState(posPortal);
                            if (stateToDetect.getBlock() == Blocks.PORTAL) {
                                // find the lowest portal block in current portal pattern to detect
                                int yBottom = yDetect;
                                do {
                                    --yBottom;
                                    posPortal.setY(yBottom);
                                    stateToDetect = chunk.getBlockState(posPortal);
                                } while (stateToDetect.getBlock() == Blocks.PORTAL);
                                yDetect = yBottom + 1;
                                posPortal.setY(yDetect);
                                double distSqTemp = posPortal.distanceSq(posOrigin);
                                char colorCode = '7';
                                if (distSqMin < 0.0 || distSqTemp < distSqMin) {
                                    distSqMin = distSqTemp;
                                    posResult.setPos(posPortal);
                                    if (distSqMinPool != null) {
                                        distSqMinPool.set(distSqTemp);
                                    }
                                    if (posResultPool != null) {
                                        posResultPool.setPos(posPortal);
                                    }
                                    colorCode = 'f';
                                }
                                String message = "§" + colorCode + posPortal + " : " + distSqTemp + " / " + distSqMin + "§r";
                                //server.getPlayerList().sendMessage(new TextComponentString(message), true);
                                MessageOutput.VANILLA_HOTBAR.send(message, MessageDispatcher.generic());
                            }
                        }
                    }
                }
                semaphore.release();
            } catch (InterruptedException ignored) {
            }
        };
        Thread thread = new Thread(threadGroup, task, "Portal Approach " + posOrigin);
        thread.setDaemon(true);
        return thread;
    }

    /**
     * in case multiple targets have an equal distance to the original position
     */
    @Nonnull
    protected Tuple<Double, List<BlockPos>> findAllClosestTargets(BlockPos posDestOrigin) {
        posDestOrigin = posDestOrigin.toImmutable();
        Tuple<Double, List<BlockPos>> result = targetCache.getOrDefault(posDestOrigin, null);
        if (result != null) {
            return result;
        }
        Semaphore semaphore = new Semaphore(0);
        List<BlockPos> posResultList = new ArrayList<>();
        AtomicDouble distSqMinPool = new AtomicDouble(-1.0);
        Thread thread = asyncTaskFindAllClosestTargets(posDestOrigin, semaphore, posResultList, distSqMinPool, null);
        thread.start();
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(45L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
        }
        if (!acquired) {
            posResultList.clear();
            distSqMinPool.set(-1.0);
            thread.interrupt();
            server.getPlayerList().sendMessage(new TextComponentString("§cFailed to check nearest portal...§r"), true);
            try {
                thread.join();
            } catch (InterruptedException e) {
                Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
            } finally {
                server.getPlayerList().sendMessage(new TextComponentString("§cStopped portal checking§r"), true);
            }
            return new Tuple<>(-1.0, Collections.emptyList());
        }
        result = new Tuple<>(distSqMinPool.get(), ImmutableList.copyOf(posResultList));
        targetCache.put(posDestOrigin, result);
        return result;
    }

    @Nullable
    protected BlockPos findUniqueClosestTarget(BlockPos posDestOrigin) {
        Tuple<Double, List<BlockPos>> tuple = findAllClosestTargets(posDestOrigin);
        if (tuple.getFirst() >= 0.0) {
            List<BlockPos> list = tuple.getSecond();
            if (list.size() == 1) {
                return list.get(0).toImmutable();
            }
        }
        return null;
    }

    @ParametersAreNonnullByDefault
    protected Thread asyncTaskFindAllClosestTargets(BlockPos posDestOrigin, Semaphore semaphore, List<BlockPos> posResultList, AtomicDouble distSqMinPool, @SuppressWarnings("SameParameterValue") @Nullable ThreadGroup threadGroup) {
        BlockPos posOrigin = posDestOrigin.toImmutable();
        Runnable task = () -> {
            try {
                ChunkReader reader = ChunkReader.getAccessTo(worldDest);
                double distSqMin = -1.0;
                if (reader == null) {
                    return;
                }
                int actualLimit = worldDest.getActualHeight() - 1;
                BlockPos.MutableBlockPos posPortal = new BlockPos.MutableBlockPos();
                for (int bx = -128; bx <= 128; ++bx) {
                    int xDetect = posOrigin.getX() + bx;
                    for (int bz = -128; bz <= 128; ++bz) {
                        int zDetect = posOrigin.getZ() + bz;
                        posPortal.setPos(xDetect, 0, zDetect);
                        Chunk chunk = reader.spectateLoadedChunk(posPortal, true);
                        if (chunk == null) {
                            throw new InterruptedException("Empty chunk time out!");
                        }
                        for (int yDetect = actualLimit; yDetect >= 0; --yDetect) {
                            posPortal.setY(yDetect);
                            IBlockState stateToDetect = chunk.getBlockState(posPortal);
                            if (stateToDetect.getBlock() == Blocks.PORTAL) {
                                // find the lowest portal block in current portal pattern to detect
                                int yBottom = yDetect;
                                do {
                                    --yBottom;
                                    posPortal.setY(yBottom);
                                    stateToDetect = chunk.getBlockState(posPortal);
                                } while (stateToDetect.getBlock() == Blocks.PORTAL);
                                yDetect = yBottom + 1;
                                posPortal.setY(yDetect);
                                double distSqTemp = posPortal.distanceSq(posOrigin);
                                char colorCode = '7';
                                if (distSqMin < 0.0 || distSqTemp <= distSqMin) {
                                    if (distSqTemp != distSqMin) {
                                        distSqMin = distSqTemp;
                                        posResultList.clear();
                                        distSqMinPool.set(distSqMin);
                                    }
                                    BlockPos posPossible = posPortal.toImmutable();
                                    posResultList.add(posPossible);
                                    colorCode = 'f';
                                }
                                server.getPlayerList().sendMessage(new TextComponentString("§" + colorCode + posPortal + " : " + distSqTemp + " / " + distSqMin + "§r"), true);
                            }
                        }
                    }
                }
                semaphore.release();
            } catch (InterruptedException ignored) {
            }
        };
        Thread thread = new Thread(threadGroup, task, "Portal Approach " + posOrigin);
        thread.setDaemon(true);
        return thread;
    }

    protected abstract void initialize();

    protected void initSetWorld() {
        DimensionType dimDest;
        switch (dimSource) {
            case NETHER:
                dimDest = DimensionType.OVERWORLD;
                break;
            case OVERWORLD:
            default:
                dimDest = DimensionType.NETHER;
                break;
        }
        worldDest = server.getWorld(dimDest.getId());
    }

    protected BlockPos getMappingBlockFrom(double x, double y, double z, boolean isCeil) {
        if (isCeil) {
            // TODO: 2023/9/21,0021 compute the "adjacent" double which is just a "bit" less than the param, according to IEEE 754
        }
        switch (dimSource) {
            case NETHER:
                x *= INTER_DIM_RATE;
                z *= INTER_DIM_RATE;
                break;
            case OVERWORLD:
            default:
                x /= INTER_DIM_RATE;
                z /= INTER_DIM_RATE;
                break;
        }
        WorldBorder borderObj = worldDest.getWorldBorder();
        x = MathHelper.clamp(x, borderObj.minX() + BORDER_WIDTH, borderObj.maxX() - BORDER_WIDTH);
        z = MathHelper.clamp(z, borderObj.minZ() + BORDER_WIDTH, borderObj.maxZ() - BORDER_WIDTH);
        x = MathHelper.clamp((int) x, -BORDER_POS, BORDER_POS);
        z = MathHelper.clamp((int) z, -BORDER_POS, BORDER_POS);
        return new BlockPos(x, y, z);
    }

    protected AxisAlignedBB getMappingAreaTo(@Nonnull BlockPos blockPos) {
        return getMappingAreaTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    protected AxisAlignedBB getMappingAreaTo(int blockX, int blockY, int blockZ) {
        WorldBorder borderObj = worldDest.getWorldBorder();
        double minX = blockX;
        if (blockX <= -BORDER_POS || minX <= borderObj.minX() + BORDER_WIDTH) {
            minX = -Double.MAX_VALUE;
        }
        double minZ = blockZ;
        if (blockZ <= -BORDER_POS || minZ <= borderObj.minZ() + BORDER_WIDTH) {
            minZ = -Double.MAX_VALUE;
        }
        double maxX = blockX + 1;
        if (blockX >= BORDER_POS || maxX >= borderObj.maxX() - BORDER_WIDTH) {
            maxX = Double.MAX_VALUE;
        }
        double maxZ = blockZ + 1;
        if (blockZ >= BORDER_POS || maxZ >= borderObj.maxZ() - BORDER_WIDTH) {
            maxZ = Double.MAX_VALUE;
        }
        switch (dimSource) {
            case NETHER:
                minX /= INTER_DIM_RATE;
                minZ /= INTER_DIM_RATE;
                maxX /= INTER_DIM_RATE;
                maxZ /= INTER_DIM_RATE;
                break;
            case OVERWORLD:
            default:
                minX *= INTER_DIM_RATE;
                minZ *= INTER_DIM_RATE;
                maxX *= INTER_DIM_RATE;
                maxZ *= INTER_DIM_RATE;
                break;
        }
        double maxY = blockY + 1;
        return new AxisAlignedBB(minX, blockY, minZ, maxX, maxY, maxZ);
    }
}
