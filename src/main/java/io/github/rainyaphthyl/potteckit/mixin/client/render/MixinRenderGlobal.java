package io.github.rainyaphthyl.potteckit.mixin.client.render;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
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
