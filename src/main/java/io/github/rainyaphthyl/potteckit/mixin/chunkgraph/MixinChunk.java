package io.github.rainyaphthyl.potteckit.mixin.chunkgraph;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkLoadSource;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk {
    @Shadow
    @Final
    public int x;
    @Shadow
    @Final
    public int z;
    @Shadow
    @Final
    private World world;

    @Inject(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;populate(II)V"))
    public void onActuallyPopulate(IChunkGenerator generator, CallbackInfo ci) {
        if (Configs.chunkLoadingGraph.getBooleanValue() && Configs.enablePotteckit.getBooleanValue() && world instanceof WorldServer) {
            MinecraftServer server = world.getMinecraftServer();
            ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
            if (server != null) {
                DimensionType dimensionType = world.provider.getDimensionType();
                MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                TickRecord tickStamp = phaseClock.markCurrentTickStamp();
                PlayerList playerList = server.getPlayerList();
                ChunkLoadCaptor.debugChunkTickStamp(tickStamp, x, z, dimensionType, ChunkEvent.POPULATING, source, playerList);
            }
        }
    }
}
