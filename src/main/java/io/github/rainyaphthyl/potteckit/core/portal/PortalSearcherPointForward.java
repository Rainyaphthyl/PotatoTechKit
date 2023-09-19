package io.github.rainyaphthyl.potteckit.core.portal;

import io.github.rainyaphthyl.potteckit.core.SilentChunkReader;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PortalSearcherPointForward extends PortalSearcher {
    private final Lock lock = new ReentrantLock();
    private final DimensionType dimSource;
    private final Vec3d posSource;
    private boolean initialized = false;
    private IBlockAccess reader = null;
    private WorldServer world = null;
    private BlockPos posDestOrigin = null;
    private BlockPos posDestTarget = null;
    private double distSqCache = 0.0;

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
                findClosestDestination();
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
        initialized = false;
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
        reader = SilentChunkReader.getAccessTo(world);
        posDestOrigin = clampTeleportDestination(x, y, z);
        initialized = true;
    }

    /**
     * Simulates vanilla searching
     */
    private void findClosestDestination() {
        if (!initialized) {
            return;
        }
        int actualLimit = world.getActualHeight() - 1;
        BlockPos.MutableBlockPos posResult = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos posPortal = new BlockPos.MutableBlockPos();
        double distSqMin = -1.0;
        for (int bx = -128; bx <= 128; ++bx) {
            int xDetect = posDestOrigin.getX() + bx;
            for (int bz = -128; bz <= 128; ++bz) {
                if ((bz & 0xF) == 0) {
                    server.getPlayerList().sendMessage(new TextComponentString("Progress: " + bx + ", " + bz));
                }
                int zDetect = posDestOrigin.getZ() + bz;
                posPortal.setPos(xDetect, 0, zDetect);
                for (int yDetect = actualLimit; yDetect >= 0; --yDetect) {
                    posPortal.setY(yDetect);
                    IBlockState stateToDetect = reader.getBlockState(posPortal);
                    if (stateToDetect.getBlock() == Blocks.PORTAL) {
                        // find the lowest portal block in current portal pattern to detect
                        int yBottom = yDetect;
                        do {
                            --yBottom;
                            posPortal.setY(yBottom);
                            stateToDetect = reader.getBlockState(posPortal);
                        } while (stateToDetect.getBlock() == Blocks.PORTAL);
                        yDetect = yBottom + 1;
                        posPortal.setY(yDetect);
                        double distSqTemp = posPortal.distanceSq(posDestOrigin);
                        if (distSqMin < 0.0 || distSqTemp < distSqMin) {
                            distSqMin = distSqTemp;
                            posResult.setPos(posPortal);
                        }
                    }
                }
            }
        }
        distSqCache = distSqMin;
        posDestTarget = posResult.toImmutable();
    }
}
