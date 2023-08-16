package io.github.rainyaphthyl.potteckit.mixin.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadReason;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.ChunkPos;
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
            world.profiler.startSection("litemods");
            // chunk == null -> generating new chunk;
            // chunk != null -> loading chunk from region file;
            Chunk chunk = cir.getReturnValue();
            if (chunk != null) {
                ChunkLoadSource source = ChunkLoadCaptor.popThreadSource();
                ChunkLoadReason reason = null;
                ChunkPos priorPos = null;
                if (source != null) {
                    priorPos = source.chunkPos;
                    reason = source.reason;
                }
                //region debug
                MinecraftServer server = world.getMinecraftServer();
                if (server != null) {
                    PlayerList playerList = server.getPlayerList();
                    TextFormatting color;
                    DimensionType dimensionType = world.provider.getDimensionType();
                    if (source == null) {
                        color = TextFormatting.GOLD;
                    } else switch (dimensionType) {
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
                    Style style = new Style().setColor(color);
                    playerList.sendMessage(new TextComponentString("(" + dimensionType.getId() + ") " + chunk.getPos() + " is loaded by " + priorPos + " via " + reason).setStyle(style));
                }
                //endregion
            }
            world.profiler.endSection();
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
