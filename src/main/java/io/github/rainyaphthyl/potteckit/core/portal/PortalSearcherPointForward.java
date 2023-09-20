package io.github.rainyaphthyl.potteckit.core.portal;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.rainyaphthyl.potteckit.core.SilentChunkReader;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PortalSearcherPointForward extends PortalSearcher {
    private final Lock lock = new ReentrantLock();
    private final DimensionType dimSource;
    private final Vec3d posSource;
    private WorldServer world = null;
    private BlockPos posDestOrigin = null;

    public PortalSearcherPointForward(MinecraftServer server, Vec3d posSource, DimensionType dimSource) {
        super(server);
        this.posSource = posSource;
        this.dimSource = dimSource;
    }

    @Override
    public void run() {
        if (lock.tryLock()) {
            try {
                Objects.requireNonNull(server);
                Objects.requireNonNull(posSource);
                Objects.requireNonNull(dimSource);
                initDestOrigin();
                Tuple<BlockPos, Double> result = findClosestDestination(posDestOrigin);
                double distSqCache = result.getSecond();
                BlockPos posDestTarget = result.getFirst();
                String message = String.format("Destination Block: %s, distance: %.1f", posDestTarget, Math.sqrt(distSqCache));
                server.getPlayerList().sendMessage(new TextComponentString(message));
                Reference.LOGGER.info(message);
            } finally {
                lock.unlock();
            }
        }
    }

    @Nonnull
    private BlockPos clampTeleportDestination(double x, double y, double z) throws NullPointerException {
        WorldBorder borderObj = world.getWorldBorder();
        x = MathHelper.clamp(x, borderObj.minX() + BORDER_WIDTH, borderObj.maxX() - BORDER_WIDTH);
        z = MathHelper.clamp(z, borderObj.minZ() + BORDER_WIDTH, borderObj.maxZ() - BORDER_WIDTH);
        x = MathHelper.clamp((int) x, -BORDER_POS, BORDER_POS);
        z = MathHelper.clamp((int) z, -BORDER_POS, BORDER_POS);
        return new BlockPos(x, y, z);
    }

    private void initDestOrigin() {
        double x = posSource.x;
        double y = posSource.y;
        double z = posSource.z;
        switch (dimSource) {
            // TODO: 2023/9/19,0019 Check where to put "case THE_END:"
            case NETHER:
                x *= INTER_DIM_RATE;
                z *= INTER_DIM_RATE;
                world = server.getWorld(DimensionType.OVERWORLD.getId());
                break;
            case OVERWORLD:
                x /= INTER_DIM_RATE;
                z /= INTER_DIM_RATE;
                world = server.getWorld(DimensionType.NETHER.getId());
                break;
            default:
                return;
        }
        posDestOrigin = clampTeleportDestination(x, y, z);
    }

    /**
     * Simulates vanilla searching
     *
     * @return the destination block and the respective minimal distance squared
     */
    @Nonnull
    private Tuple<BlockPos, Double> findClosestDestination(BlockPos posDestOrigin) {
        Semaphore semaphore = new Semaphore(0);
        BlockPos.MutableBlockPos posResult = new BlockPos.MutableBlockPos();
        AtomicDouble distSqMin = new AtomicDouble(-1.0);
        Runnable task = () -> {
            try {
                SilentChunkReader reader = SilentChunkReader.getAccessTo(world);
                int actualLimit = world.getActualHeight() - 1;
                BlockPos.MutableBlockPos posPortal = new BlockPos.MutableBlockPos();
                for (int bx = -128; bx <= 128; ++bx) {
                    int xDetect = posDestOrigin.getX() + bx;
                    for (int bz = -128; bz <= 128; ++bz) {
                        int zDetect = posDestOrigin.getZ() + bz;
                        posPortal.setPos(xDetect, 0, zDetect);
                        for (int yDetect = actualLimit; yDetect >= 0; --yDetect) {
                            posPortal.setY(yDetect);
                            Chunk chunk = reader.spectateLoadedChunk(posPortal, true);
                            if (chunk == null) {
                                throw new InterruptedException("Empty chunk time out!");
                            }
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
                                double distSqTemp = posPortal.distanceSq(posDestOrigin);
                                char colorCode = '7';
                                if (distSqMin.get() < 0.0 || distSqTemp < distSqMin.get()) {
                                    distSqMin.set(distSqTemp);
                                    posResult.setPos(posPortal);
                                    colorCode = 'f';
                                }
                                server.getPlayerList().sendMessage(new TextComponentString("§" + colorCode + posPortal + " : " + distSqTemp + " / " + distSqMin + "§r"), true);
                            }
                        }
                    }
                }
                semaphore.release();
            } catch (InterruptedException e) {
                Reference.LOGGER.warn("Interrupted: {}: {}", this, e);
            } finally {
                Reference.LOGGER.warn("Finished: {}", this);
            }
        };
        Thread thread = new Thread(task, "Portal Approach");
        thread.setDaemon(true);
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
}
