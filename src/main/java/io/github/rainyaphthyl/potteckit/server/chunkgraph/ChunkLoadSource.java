package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import net.minecraft.util.math.ChunkPos;

import java.util.Arrays;
import java.util.Objects;

public class ChunkLoadSource {
    public final ChunkPos chunkPos;
    public final ChunkLoadReason reason;
    public final Object[] otherArgs;

    public ChunkLoadSource(int chunkX, int chunkZ, ChunkLoadReason reason, Object... otherArgs) {
        this(new ChunkPos(chunkX, chunkZ), reason, otherArgs);
    }

    public ChunkLoadSource(ChunkPos chunkPos, ChunkLoadReason reason, Object... otherArgs) {
        this.chunkPos = chunkPos;
        this.reason = reason;
        this.otherArgs = otherArgs;
    }

    @Override
    public String toString() {
        return "{" + chunkPos + ':' + reason + ':' + Arrays.deepToString(otherArgs) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkLoadSource)) return false;
        ChunkLoadSource source = (ChunkLoadSource) o;
        if (!Objects.equals(chunkPos, source.chunkPos)) return false;
        if (reason != source.reason) return false;
        return Arrays.deepEquals(otherArgs, source.otherArgs);
    }

    @Override
    public int hashCode() {
        int result = chunkPos != null ? chunkPos.hashCode() : 0;
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + Arrays.deepHashCode(otherArgs);
        return result;
    }
}
