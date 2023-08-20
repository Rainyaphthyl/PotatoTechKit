package io.github.rainyaphthyl.potteckit.mixin.client.render;

import io.github.rainyaphthyl.potteckit.client.RenderGlobals;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessChunkRenderWorker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import java.util.concurrent.Semaphore;

@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher {
    @Unique
    public final Profiler potatoTechKit$profiler = Minecraft.getMinecraft().profiler;
    @Shadow
    @Final
    private ChunkRenderWorker renderWorker;
    @Unique
    private ChunkRenderDispatcher potatoTechKit$self;

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

    @Redirect(method = "updateChunkNow", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;renderWorker:Lnet/minecraft/client/renderer/chunk/ChunkRenderWorker;"))
    public ChunkRenderWorker captureSelf(ChunkRenderDispatcher instance) {
        if (RenderGlobals.asyncImmediate.get()) {
            potatoTechKit$self = instance;
        }
        return renderWorker;
    }

    @Redirect(method = "updateChunkNow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderWorker;processTask(Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V"))
    public void asyncRenderChunk(ChunkRenderWorker instance, ChunkCompileTaskGenerator generator) {
        if (RenderGlobals.asyncImmediate.get() && Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            ChunkRenderWorker worker = new ChunkRenderWorker(potatoTechKit$self, generator.getRegionRenderCacheBuilder());
            Thread threadMain = Thread.currentThread();
            Semaphore semaphore = RenderGlobals.semaphores.get(threadMain);
            RenderGlobals.sectionNum.put(threadMain, RenderGlobals.sectionNum.get(threadMain) + 1);
            Thread threadAsync = new Thread(() -> {
                if (worker instanceof AccessChunkRenderWorker) {
                    ((AccessChunkRenderWorker) worker).invokeProcessTask(generator);
                }
                if (semaphore != null) {
                    semaphore.release();
                }
            });
            threadAsync.setName("Chunk Renderer " + threadAsync.getId());
            threadAsync.setDaemon(true);
            threadAsync.start();
        } else {
            ((AccessChunkRenderWorker) instance).invokeProcessTask(generator);
        }
    }
}
