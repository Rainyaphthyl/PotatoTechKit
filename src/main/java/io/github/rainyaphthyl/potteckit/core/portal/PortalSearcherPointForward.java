package io.github.rainyaphthyl.potteckit.core.portal;

import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;

import java.util.Objects;

public class PortalSearcherPointForward extends PortalSearcher {
    private final Vec3d posSource;
    private BlockPos posDestOrigin = null;

    public PortalSearcherPointForward(MinecraftServer server, DimensionType dimSource, Vec3d posSource) {
        super(server, dimSource);
        this.posSource = Objects.requireNonNull(posSource);
    }

    @Override
    public void run() {
        if (lock.tryLock()) {
            try {
                initialize();
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

    @Override
    protected void initialize() {
        initSetWorld();
        posDestOrigin = getMappingBlockFrom(posSource.x, posSource.y, posSource.z, false);
    }
}
