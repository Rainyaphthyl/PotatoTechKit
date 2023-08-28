package io.github.rainyaphthyl.potteckit.mixin.render;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessMinecraft;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessRenderChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
    /**
     * Called only from the Minecraft Client Thread
     */
    @Unique
    private boolean potatoTechKit$timeOut = false;
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "setupTerrain", at = @At(value = "HEAD"))
    public void resetPendingTag(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        if (potatoTechKit$timeOut && mc.isCallingFromMinecraftThread() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$timeOut = false;
        }
    }

    @Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;chunksToUpdate:Ljava/util/Set;", opcode = Opcodes.GETFIELD, ordinal = 1), cancellable = true)
    public void startYeetChunkRebuild(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && mc.isCallingFromMinecraftThread()) {
            boolean shouldYeet = potatoTechKit$checkYeetRequire();
            if (shouldYeet && ci.isCancellable() && !ci.isCancelled()) {
                ci.cancel();
                if (ci.isCancelled()) {
                    mc.profiler.endSection();
                }
            }
        }
    }

    @Unique
    private boolean potatoTechKit$checkYeetRequire() {
        if (mc.isGamePaused()) {
            return false;
        } else {
            return Configs.yeetChunkRebuild.getBooleanValue();
        }
    }

    @Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;displayListEntitiesDirty:Z", opcode = Opcodes.PUTFIELD, ordinal = 2), cancellable = true)
    public void checkTimeOut(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        if ((Configs.chunkRebuildBuffer.getBooleanValue() || Configs.autoDisturbChunkRebuild.getBooleanValue())
                && mc.isCallingFromMinecraftThread() && Configs.enablePotteckit.getBooleanValue()) {
            if (mc.isGamePaused()) {
                potatoTechKit$timeOut = false;
            } else {
                boolean shouldYeet = potatoTechKit$timeOut;
                if (!shouldYeet) {
                    long prev = ((AccessMinecraft) mc).getStartNanoTime();
                    long curr = System.nanoTime();
                    long duration = curr - prev;
                    double threshold = Configs.chunkRebuildBufferThreshold.getInverseValue();
                    shouldYeet = duration > threshold;
                }
                if (shouldYeet) {
                    potatoTechKit$timeOut = true;
                    if (Configs.autoDisturbChunkRebuild.getBooleanValue() && ci.isCancellable() && !ci.isCancelled()) {
                        ci.cancel();
                        if (ci.isCancelled()) {
                            mc.profiler.endSection();
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;getPosition()Lnet/minecraft/util/math/BlockPos;"))
    public BlockPos setImmediateUpdate(RenderChunk instance) {
        if (potatoTechKit$timeOut && mc.isCallingFromMinecraftThread() && instance instanceof AccessRenderChunk) {
            ((AccessRenderChunk) instance).setNeedImmediate(false);
        }
        return instance.getPosition();
    }

    @ModifyConstant(method = "setupTerrain", constant = @Constant(doubleValue = 768.0))
    public double setPlayerDistance(double constant) {
        if (potatoTechKit$timeOut && mc.isCallingFromMinecraftThread()) {
            return -1.0;
        } else {
            return constant;
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;updateChunkNow(Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z"))
    public boolean profileChunkRebuild(@Nonnull ChunkRenderDispatcher instance, RenderChunk renderChunk) {
        if (Configs.enablePotteckit.getBooleanValue()) {
            long timeStart = System.nanoTime();
            boolean flag;
            if (Configs.asyncNearbyChunkRender.getBooleanValue()) {
                ((AccessRenderChunk) renderChunk).setNeedImmediate(false);
                flag = instance.updateChunkLater(renderChunk);
            } else {
                flag = instance.updateChunkNow(renderChunk);
            }
            if (Configs.profileImmediateChunkRebuild.getBooleanValue()) {
                long timeEnd = System.nanoTime();
                long duration = timeEnd - timeStart;
                double millis = duration / 1.0e6;
                MessageOutput.CHAT.send("Chunk at " + renderChunk.getPosition() + " took " + String.format("%.3f", millis) + " ms", MessageDispatcher.generic());
            }
            return flag;
        } else {
            return instance.updateChunkNow(renderChunk);
        }
    }
}