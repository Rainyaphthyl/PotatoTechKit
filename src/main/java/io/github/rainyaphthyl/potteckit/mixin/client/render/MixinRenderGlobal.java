package io.github.rainyaphthyl.potteckit.mixin.client.render;

import com.google.common.collect.Sets;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;chunksToUpdate:Ljava/util/Set;", opcode = Opcodes.GETFIELD, ordinal = 1), cancellable = true)
    public void startYeetChunkRebuild(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        if (Configs.yeetChunkRebuild.getBooleanValue() && Configs.enablePotteckit.getBooleanValue() && mc.isCallingFromMinecraftThread()) {
            if (ci.isCancellable() && !ci.isCancelled()) {
                Set<RenderChunk> set = chunksToUpdate;
                chunksToUpdate = Sets.newLinkedHashSet();
                chunksToUpdate.addAll(set);
                ci.cancel();
                if (ci.isCancelled()) {
                    Profiler profiler = mc.profiler;
                    if ("rebuildNear".equals(profiler.getNameOfLastSection())) {
                        profiler.endSection();
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
