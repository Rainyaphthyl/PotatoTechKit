package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkRenderWorker.class)
public interface AccessChunkRenderWorker {
    @Invoker(value = "processTask")
    void invokeProcessTask(final ChunkCompileTaskGenerator generator);
}
