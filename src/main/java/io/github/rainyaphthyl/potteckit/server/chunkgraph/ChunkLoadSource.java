package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import java.util.Arrays;

public class ChunkLoadSource {
    public final int chunkX;
    public final int chunkZ;
    public final ChunkLoadReason reason;
    public final Object[] otherArgs;

    public ChunkLoadSource(int chunkX, int chunkZ, ChunkLoadReason reason, Object... otherArgs) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.reason = reason;
        this.otherArgs = otherArgs;
    }

    @Override
    public String toString() {
        return "[" + chunkX + ',' + chunkZ + ':' + reason + ']' + ':' + Arrays.deepToString(otherArgs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkLoadSource)) return false;
        ChunkLoadSource that = (ChunkLoadSource) o;
        if (chunkX != that.chunkX) return false;
        if (chunkZ != that.chunkZ) return false;
        if (reason != that.reason) return false;
        return Arrays.deepEquals(otherArgs, that.otherArgs);
    }

    @Override
    public int hashCode() {
        int result = chunkX;
        result = 31 * result + chunkZ;
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + Arrays.deepHashCode(otherArgs);
        return result;
    }
}
