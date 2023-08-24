package io.github.rainyaphthyl.potteckit.mixin.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadSource;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import io.github.rainyaphthyl.potteckit.server.phaseclock.PhaseRecord;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
    @Shadow
    @Final
    private WorldServer world;

    @Inject(method = "loadChunkFromFile", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getTotalWorldTime()J"))
    public void onLoadChunkFromFile(int x, int z, CallbackInfoReturnable<Chunk> cir, Chunk chunk) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                PhaseRecord record = phaseClock.getRecord();
                //region debug
                ChunkLoadCaptor.debugOnChat(server.getTickCounter(), record, chunk.getPos(), dimensionType, ChunkEvent.LOADING, source, server.getPlayerList());
                //endregion
            }
        }
    }

    @Inject(method = "getLoadedChunk", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;unloadQueued:Z", opcode = Opcodes.PUTFIELD))
    public void onGetLoadedChunk(int x, int z, CallbackInfoReturnable<Chunk> cir, long i, Chunk chunk) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null && chunk.unloadQueued) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                PhaseRecord record = phaseClock.getRecord();
                //region debug
                ChunkLoadCaptor.debugOnChat(server.getTickCounter(), record, chunk.getPos(), dimensionType, ChunkEvent.CANCEL_UNLOAD, source, server.getPlayerList());
                //endregion
            }
        }
    }

    @Inject(method = "queueUnload", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;unloadQueued:Z", opcode = Opcodes.PUTFIELD))
    public void onQueueUnload(Chunk chunkIn, CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null && !chunkIn.unloadQueued) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                PhaseRecord record = phaseClock.getRecord();
                //region debug
                ChunkLoadCaptor.debugOnChat(server.getTickCounter(), record, chunkIn.getPos(), dimensionType, ChunkEvent.QUEUE_UNLOAD, source, server.getPlayerList());
                //endregion
            }
        }
    }

    @Inject(method = "tick", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;onUnload()V"))
    public void onChunkUnload(CallbackInfoReturnable<Boolean> cir, Iterator<Long> iterator, int i, Long olong, Chunk chunk) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                PhaseRecord record = phaseClock.getRecord();
                //region debug
                ChunkLoadCaptor.debugOnChat(server.getTickCounter(), record, chunk.getPos(), dimensionType, ChunkEvent.UNLOADING, source, server.getPlayerList());
                //endregion
            }
        }
    }

    @Inject(method = "provideChunk", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;generateChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    public void onGenerateChunk(int x, int z, CallbackInfoReturnable<Chunk> cir, Chunk chunk, long i) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                PhaseRecord record = phaseClock.getRecord();
                //region debug
                ChunkLoadCaptor.debugOnChat(server.getTickCounter(), record, new ChunkPos(x, z), dimensionType, ChunkEvent.GENERATING, source, server.getPlayerList());
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
