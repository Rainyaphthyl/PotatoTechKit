package io.github.rainyaphthyl.potteckit.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SortedRenderChunk implements Comparable<SortedRenderChunk> {
    public static final Object2ObjectMap<BlockPos, SortedRenderChunk> POOL = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    static {
        POOL.defaultReturnValue(null);
    }

    public final RenderChunk parent;

    private SortedRenderChunk(RenderChunk parent) {
        this.parent = Objects.requireNonNull(parent);
    }

    public static SortedRenderChunk getInstanceAt(BlockPos pos) {
        synchronized (POOL) {
            return POOL.get(pos);
        }
    }

    public static SortedRenderChunk getInstanceOf(RenderChunk parent) {
        if (parent == null) {
            return null;
        }
        SortedRenderChunk section;
        BlockPos pos = parent.getPosition();
        synchronized (POOL) {
            section = POOL.get(pos);
            if (section == null) {
                section = new SortedRenderChunk(parent);
                POOL.put(pos.toImmutable(), section);
            }
        }
        return section;
    }

    @Override
    public int compareTo(@Nonnull SortedRenderChunk o) {
        return parent.getPosition().compareTo(o.parent.getPosition());
    }

    @Override
    public int hashCode() {
        return parent.getPosition().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof SortedRenderChunk) {
            return parent.getPosition().equals(((SortedRenderChunk) obj).parent.getPosition());
        } else if (obj instanceof RenderChunk) {
            return parent.getPosition().equals(((RenderChunk) obj).getPosition());
        } else {
            return false;
        }
    }
}
