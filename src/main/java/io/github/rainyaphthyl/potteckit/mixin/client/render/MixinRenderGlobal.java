package io.github.rainyaphthyl.potteckit.mixin.client.render;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.FrameTimer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.Set;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
    @Shadow
    private Set<RenderChunk> chunksToUpdate;
    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private boolean potatoTechKit$checkYeetRequire() {
        if (mc.isGamePaused()) {
            return false;
        } else if (Configs.yeetChunkRebuild.getBooleanValue()) {
            return true;
        } else if (Configs.autoDisturbChunkRebuild.getBooleanValue()) {
            boolean shouldYeet = false;
            FrameTimer frameTimer = mc.frameTimer;
            long[] frames = frameTimer.getFrames();
            int index = frameTimer.parseIndex(frameTimer.getIndex() + frames.length - 1);
            long currNanos = frames[index];
            if (currNanos > Configs.chunkRebuildBufferThreshold.getInverseValue()) {
                shouldYeet = true;
                String message = "Yeet chunk rebuild: " + String.format("%.2f", currNanos * 1.0e-6) + " ms/f > "
                        + String.format("%.2f", Configs.chunkRebuildBufferThreshold.getInverseValue() * 1.0e-6) + " ms/f";
                //MessageOutput.CHAT.send(message, MessageDispatcher.warning());
                Reference.LOGGER.info(message);
            }
            return shouldYeet;
        } else {
            return false;
        }
    }

    @Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;chunksToUpdate:Ljava/util/Set;", opcode = Opcodes.GETFIELD, ordinal = 1), cancellable = true)
    public void startYeetChunkRebuild(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && mc.isCallingFromMinecraftThread()) {
            boolean shouldYeet = potatoTechKit$checkYeetRequire();
            if (shouldYeet) {
                if (ci.isCancellable() && !ci.isCancelled()) {
                    //Set<RenderChunk> set = chunksToUpdate;
                    //chunksToUpdate = Sets.newLinkedHashSet();
                    //chunksToUpdate.addAll(set);
                    ci.cancel();
                    if (ci.isCancelled()) {
                        Profiler profiler = mc.profiler;
                        if (profiler.profilingEnabled && "rebuildNear".equals(profiler.getNameOfLastSection())) {
                            profiler.endSection();
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;updateChunkNow(Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z"))
    public boolean profileChunkRebuild(@Nonnull ChunkRenderDispatcher instance, RenderChunk renderChunk) {
        if (Configs.profileImmediateChunkRebuild.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            long timeStart = System.nanoTime();
            boolean flag = instance.updateChunkNow(renderChunk);
            long timeEnd = System.nanoTime();
            long duration = timeEnd - timeStart;
            double millis = duration / 1.0e6;
            MessageOutput.CHAT.send("Chunk at " + renderChunk.getPosition() + " took " + String.format("%.3f", millis) + " ms", MessageDispatcher.generic());
            return flag;
        } else {
            return instance.updateChunkNow(renderChunk);
        }
    }
}
