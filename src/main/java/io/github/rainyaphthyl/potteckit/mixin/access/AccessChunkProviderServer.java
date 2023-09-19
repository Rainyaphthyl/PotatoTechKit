package io.github.rainyaphthyl.potteckit.mixin.access;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkProviderServer.class)
public interface AccessChunkProviderServer {
    @Accessor(value = "loadedChunks")
    Long2ObjectMap<Chunk> getLoadedChunksMap();

    @Accessor(value = "chunkLoader")
    IChunkLoader getChunkLoader();
}
