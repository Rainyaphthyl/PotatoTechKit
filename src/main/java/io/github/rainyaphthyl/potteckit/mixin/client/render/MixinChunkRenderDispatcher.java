package io.github.rainyaphthyl.potteckit.mixin.client.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher {
    @Unique
    public final Profiler potatoTechKit$profiler = Minecraft.getMinecraft().profiler;

    @Redirect(method = "updateChunkNow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;makeCompileTaskChunk()Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;"))
    public ChunkCompileTaskGenerator profileCompile(@Nonnull RenderChunk chunkRenderer) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.moreProfilerLevels.getBooleanValue() && Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            potatoTechKit$profiler.startSection("makeCompileTask");
            ChunkCompileTaskGenerator generator = chunkRenderer.makeCompileTaskChunk();
            potatoTechKit$profiler.endSection();
            return generator;
        } else {
            return chunkRenderer.makeCompileTaskChunk();
        }
    }
}
