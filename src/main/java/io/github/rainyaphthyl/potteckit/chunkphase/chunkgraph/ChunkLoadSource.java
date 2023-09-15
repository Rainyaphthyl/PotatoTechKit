package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;

import java.util.Arrays;
import java.util.Objects;

public class ChunkLoadSource {
    public final ChunkPos chunkPos;
    public final DimensionType dimensionType;
    public final ChunkLoadReason reason;
    public final Object[] otherArgs;

    public ChunkLoadSource(int chunkX, int chunkZ, DimensionType dimensionType, ChunkLoadReason reason, Object... otherArgs) {
        this(new ChunkPos(chunkX, chunkZ), dimensionType, reason, otherArgs);
    }

    public ChunkLoadSource(ChunkPos chunkPos, DimensionType dimensionType, ChunkLoadReason reason, Object... otherArgs) {
        this.chunkPos = chunkPos;
        this.dimensionType = dimensionType;
        this.reason = reason;
        this.otherArgs = otherArgs;
    }

    @Override
    public String toString() {
        return "{" + TickRecord.getDimensionChar(dimensionType) + ':' + chunkPos + ':' + reason + ':' + Arrays.deepToString(otherArgs) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkLoadSource)) return false;
        ChunkLoadSource source = (ChunkLoadSource) o;
        if (!Objects.equals(chunkPos, source.chunkPos)) return false;
        if (dimensionType != source.dimensionType) return false;
        if (reason != source.reason) return false;
        return Arrays.deepEquals(otherArgs, source.otherArgs);
    }

    @Override
    public int hashCode() {
        int result = chunkPos != null ? chunkPos.hashCode() : 0;
        result = 31 * result + (dimensionType != null ? dimensionType.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + Arrays.deepHashCode(otherArgs);
        return result;
    }
}
