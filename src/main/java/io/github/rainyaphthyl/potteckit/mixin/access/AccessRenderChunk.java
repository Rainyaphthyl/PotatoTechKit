package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderChunk.class)
public interface AccessRenderChunk {
    @Accessor(value = "needsImmediateUpdate")
    void setNeedImmediate(boolean immediate);
}
