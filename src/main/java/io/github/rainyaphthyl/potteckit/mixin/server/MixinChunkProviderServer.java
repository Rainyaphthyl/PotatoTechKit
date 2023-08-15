package io.github.rainyaphthyl.potteckit.mixin.server;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {
    @Inject(method = "loadChunkFromFile", at = @At(value = "RETURN", ordinal = 0))
    public void onLoadChunkFromFile(int x, int z, @Nonnull CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = cir.getReturnValue();
        // chunk == null -> generating new chunk;
        // chunk != null -> loading chunk from region file;
    }
}
