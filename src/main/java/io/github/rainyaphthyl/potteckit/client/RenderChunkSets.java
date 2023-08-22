package io.github.rainyaphthyl.potteckit.client;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RenderChunkSets {
    public static final ConcurrentMap<BlockPos, RenderChunk> sectionMap = new ConcurrentHashMap<>();

}
