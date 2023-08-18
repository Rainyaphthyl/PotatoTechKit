package io.github.rainyaphthyl.potteckit.mixin.client.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
                potatoTechKit$profiler.startSection("pending");
                potatoTechKit$pending = true;
            }
        }
    }

    @Inject(method = "processTask", at = {@At(value = "RETURN", ordinal = 0), @At(value = "RETURN", ordinal = 1), @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getMinecraft()Lnet/minecraft/client/Minecraft;", ordinal = 1)})
    public void endSectionPending(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$pending) {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.endSection();
                potatoTechKit$pending = false;
            }
        }
    }

    @Inject(method = "processTask", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
    public void startSectionRunning(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.startSection("running");
                potatoTechKit$running = true;
            }
        }
    }

    @Inject(method = "processTask", at = {@At(value = "RETURN", ordinal = 2), @At(value = "RETURN", ordinal = 3)})
    public void endSectionRunning(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$running) {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                potatoTechKit$profiler.endSection();
                potatoTechKit$running = false;
            }
        }
    }
}
