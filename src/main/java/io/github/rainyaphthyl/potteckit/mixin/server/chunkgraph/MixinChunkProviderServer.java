package io.github.rainyaphthyl.potteckit.mixin.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadReason;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadSource;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import io.github.rainyaphthyl.potteckit.server.phaseclock.PhaseRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
    @Shadow
    @Final
    private WorldServer world;

    @Unique
    private static void potatoTechKit$debugOnChat(int tickCount, PhaseRecord record, ChunkPos currPos, @Nonnull DimensionType dimensionType, ChunkLoadSource source, PlayerList playerList) {
        ITextComponent component = new TextComponentString(
                "[" + tickCount + ':' + PhaseRecord.getShortName(record) + ']'
        ).setStyle(new Style().setColor(TextFormatting.WHITE));
        ITextComponent body = new TextComponentString(" c" + currPos + " loaded");
        TextFormatting color;
        switch (dimensionType) {
            case OVERWORLD:
                color = TextFormatting.GREEN;
                break;
            case NETHER:
                color = TextFormatting.RED;
                break;
            case THE_END:
                color = TextFormatting.LIGHT_PURPLE;
                break;
            default:
                color = TextFormatting.GRAY;
        }
        body.setStyle(new Style().setColor(color));
        if (source != null) {
            ChunkPos priorPos = source.chunkPos;
            ChunkLoadReason reason = source.reason;
            ITextComponent tail = new TextComponentString(" by " + priorPos + " (" + reason + ')');
            tail.setStyle(new Style().setColor(TextFormatting.GRAY));
            body.appendSibling(tail);
        }
        component.appendSibling(body);
        playerList.sendMessage(component);
    }

    @Inject(method = "loadChunkFromFile", at = @At(value = "RETURN", ordinal = 0))
    public void onLoadChunkFromFile(int x, int z, @Nonnull CallbackInfoReturnable<Chunk> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            Profiler profiler = world.profiler;
            profiler.startSection("litemods");
            // chunk == null -> generating new chunk;
            // chunk != null -> loading chunk from region file;
            Chunk chunk = cir.getReturnValue();
            if (chunk != null) {
                MinecraftServer server = world.getMinecraftServer();
                ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
                if (server != null) {
                    DimensionType dimensionType = world.provider.getDimensionType();
                    MutablePhaseClock phaseClock = MutablePhaseClock.instanceFromServer(server);
                    PhaseRecord record = phaseClock.getRecord();
                    //region debug
                    potatoTechKit$debugOnChat(server.getTickCounter(), record, chunk.getPos(), dimensionType, source, server.getPlayerList());
                    //endregion
                }
            }
            profiler.endSection();
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
