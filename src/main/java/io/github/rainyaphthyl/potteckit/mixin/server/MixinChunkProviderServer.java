package io.github.rainyaphthyl.potteckit.mixin.server;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadReason;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
    @Shadow
    @Final
    private WorldServer world;

    @Inject(method = "loadChunkFromFile", at = @At(value = "RETURN", ordinal = 0))
    public void onLoadChunkFromFile(int x, int z, @Nonnull CallbackInfoReturnable<Chunk> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            // chunk == null -> generating new chunk;
            // chunk != null -> loading chunk from region file;
            Chunk chunk = cir.getReturnValue();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (source != null) {
                ChunkLoadReason reason = source.reason;
                //region debug
                MinecraftServer server = world.getMinecraftServer();
                if (server != null) {
                    PlayerList playerList = server.getPlayerList();
                    playerList.sendMessage(new TextComponentString(chunk.getPos() + " is loaded by " + new ChunkPos(source.chunkX, source.chunkZ) + " via " + reason));
                }
                //endregion
            }
        }
    }

    @Inject(method = "loadChunk", at = @At(value = "RETURN"))
    public void onFinishLoadChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            ChunkLoadCaptor.removeThreadSource();
        }
    }

    @Inject(method = "provideChunk", at = @At(value = "RETURN"))
    public void onFinishProvideChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            ChunkLoadCaptor.removeThreadSource();
        }
    }
}
