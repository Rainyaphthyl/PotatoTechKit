package io.github.rainyaphthyl.potteckit.mixin.access;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkProviderClient.class)
public interface AccessChunkProviderClient extends IChunkProvider {
    @Accessor(value = "loadedChunks")
    Long2ObjectMap<Chunk> getLoadedChunks();
}
