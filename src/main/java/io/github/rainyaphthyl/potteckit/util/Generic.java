package io.github.rainyaphthyl.potteckit.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;

/**
 * Static methods
 */
public class Generic {
    public static void copyFile(File src, File dest) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(src)) {
            try (FileOutputStream outputStream = new FileOutputStream(dest);
                 FileChannel inChannel = inputStream.getChannel();
                 FileChannel outChannel = outputStream.getChannel()) {
                outChannel.transferFrom(inChannel, 0, inChannel.size());
            }
        }
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static Iterable<BlockPos> getCornersInBox(final BlockPos from, final BlockPos to) {
        return getCornersInBox(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    @Nonnull
    private static Iterable<BlockPos> getCornersInBox(int x1, int y1, int z1, int x2, int y2, int z2) {
        final int xMin = Math.min(x1, x2);
        final int yMin = Math.min(y1, y2);
        final int zMin = Math.min(z1, z2);
        final int xMax = Math.max(x1, x2);
        final int yMax = Math.max(y1, y2);
        final int zMax = Math.max(z1, z2);
        return new Iterable<BlockPos>() {
            @Nonnull
            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {
                    private boolean first = true;
                    private int lastPosX;
                    private int lastPosY;
                    private int lastPosZ;

                    protected BlockPos computeNext() {
                        if (first) {
                            first = false;
                            lastPosX = xMin;
                            lastPosY = yMin;
                            lastPosZ = zMin;
                            return new BlockPos(xMin, yMin, zMin);
                        } else if (lastPosX == xMax && lastPosY == yMax && lastPosZ == zMax) {
                            return endOfData();
                        } else {
                            if (lastPosX < xMax) {
                                lastPosX = xMax;
                            } else if (lastPosY < yMax) {
                                lastPosX = xMin;
                                lastPosY = yMax;
                            } else if (lastPosZ < zMax) {
                                lastPosX = xMin;
                                lastPosY = yMin;
                                lastPosZ = zMax;
                            }
                            return new BlockPos(lastPosX, lastPosY, lastPosZ);
                        }
                    }
                };
            }
        };
    }
}
