package io.github.rainyaphthyl.potteckit.mixin.client.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderWorker.class)
public abstract class MixinChunkRenderWorker {
    @Unique
    private final Minecraft potatoTechKit$client = Minecraft.getMinecraft();
    @Unique
    public final Profiler potatoTechKit$profiler = potatoTechKit$client.profiler;
    @Unique
    private boolean potatoTechKit$pending = false;
    @Unique
    private boolean potatoTechKit$running = false;

    @Inject(method = "processTask", at = @At(value = "HEAD"))
    public void startSectionPending(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (potatoTechKit$client.isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.startSection("pendChunkRender");
                potatoTechKit$pending = true;
            }
        }
    }

    @Inject(method = "processTask", at = {@At(value = "RETURN", ordinal = 0), @At(value = "RETURN", ordinal = 1), @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getMinecraft()Lnet/minecraft/client/Minecraft;", ordinal = 1)})
    public void endSectionPending(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$pending) {
            if (potatoTechKit$client.isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.endSection();
                potatoTechKit$pending = false;
            }
        }
    }

    @Inject(method = "processTask", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
    public void startSectionRunning(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (potatoTechKit$client.isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.startSection("runChunkRender");
                potatoTechKit$running = true;
            }
        }
    }

    @Inject(method = "processTask", at = {@At(value = "RETURN", ordinal = 2), @At(value = "RETURN", ordinal = 3)})
    public void endSectionRunning(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$running) {
            if (potatoTechKit$client.isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.endSection();
                potatoTechKit$running = false;
            }
        }
    }

    @Redirect(method = "processTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;rebuildChunk(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V"))
    public void profileRebuildChunk(RenderChunk instance, float x, float y, float z, ChunkCompileTaskGenerator generator) {
        if (Configs.moreProfilerLevels.getBooleanValue() && potatoTechKit$client.isCallingFromMinecraftThread()
                && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("rebuildChunk");
            instance.rebuildChunk(x, y, z, generator);
            potatoTechKit$profiler.endSection();
        } else {
            instance.rebuildChunk(x, y, z, generator);
        }
    }

    @Redirect(method = "processTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;resortTransparency(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V"))
    public void profileResortTransparency(RenderChunk instance, float x, float y, float z, ChunkCompileTaskGenerator generator) {
        if (Configs.moreProfilerLevels.getBooleanValue() && potatoTechKit$client.isCallingFromMinecraftThread()
                && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("resortTransparency");
            instance.resortTransparency(x, y, z, generator);
            potatoTechKit$profiler.endSection();
        } else {
            instance.resortTransparency(x, y, z, generator);
        }
    }
}
