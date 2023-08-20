package io.github.rainyaphthyl.potteckit.mixin.client.render;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.config.Configs;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.lib.Opcodes;
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
    @Unique
    private final Object2DoubleMap<Thread> potatoTechKit$distSqLocals = Object2DoubleMaps.synchronize(new Object2DoubleOpenHashMap<>());
    @Unique
    private final Object2ObjectMap<Thread, RenderChunk> potatoTechKit$renderChunkLocals = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    @Shadow
    private Set<RenderChunk> chunksToUpdate;
    @Unique
    private boolean potatoTechKit$reduced = false;

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;distanceSq(Lnet/minecraft/util/math/Vec3i;)D"))
    public double captureLocalDistance(@Nonnull BlockPos instance, Vec3i posView) {
        if (Configs.reduceImmediateChunkRender.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$reduced = true;
            double distanceSq = instance.distanceSq(posView);
            potatoTechKit$distSqLocals.put(Thread.currentThread(), distanceSq);
            return distanceSq;
        } else {
            potatoTechKit$reduced = false;
            return instance.distanceSq(posView);
        }
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;needsImmediateUpdate()Z"))
    public boolean captureRenderChunk(@Nonnull RenderChunk instance) {
        if (potatoTechKit$reduced) {
            boolean flag = instance.needsImmediateUpdate();
            potatoTechKit$renderChunkLocals.put(Thread.currentThread(), instance);
            return flag;
        } else {
            return instance.needsImmediateUpdate();
        }
    }

    //@Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;mc:Lnet/minecraft/client/Minecraft;", opcode = Opcodes.GETFIELD, ordinal = 9))
    //public void pendChunkRender(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
    //}
    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;updateChunkNow(Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z"))
    public boolean onNearbyChunkRebuild(@Nonnull ChunkRenderDispatcher instance, RenderChunk renderChunk) {
        if (potatoTechKit$reduced) {
            Thread thread = Thread.currentThread();
            double distanceSq = potatoTechKit$distSqLocals.getDouble(thread);
            //RenderChunk section = potatoTechKit$renderChunkLocals.get(thread);
            double maxDistSq;
            {
                double dist = Configs.immediateChunkRenderRange.getDoubleValue();
                maxDistSq = dist * dist;
            }
            if (distanceSq < maxDistSq) {
                return instance.updateChunkNow(renderChunk);
            } else {
                Configs.ChunkRenderYeetMode mode = Configs.chunkRenderYeetMode.getValue();
                switch (mode) {
                    case FIELD:
                        renderChunk.clearNeedsUpdate();
                        renderChunk.setNeedsUpdate(false);
                        return chunksToUpdate.add(renderChunk);
                    case INVOKE:
                        return instance.updateChunkLater(renderChunk);
                }
            }
        } else if (Configs.profileImmediateChunkRebuild.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            long timeStart = System.nanoTime();
            boolean flag = instance.updateChunkNow(renderChunk);
            long timeEnd = System.nanoTime();
            long duration = timeEnd - timeStart;
            double millis = duration / 1.0e6;
            MessageOutput.CHAT.send("Chunk at " + renderChunk.getPosition() + " took " + String.format("%.3f", millis) + " ms", MessageDispatcher.generic());
            return flag;
        }
        return instance.updateChunkNow(renderChunk);
    }

    @Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;chunksToUpdate:Ljava/util/Set;", opcode = Opcodes.GETFIELD, ordinal = 3))
    public void finishImmediateRender(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
    }
}
