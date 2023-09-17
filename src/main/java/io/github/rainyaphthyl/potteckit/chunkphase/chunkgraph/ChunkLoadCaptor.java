package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.PhaseRecord;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.input.PotteckitPacketBuffer;
import io.github.rainyaphthyl.potteckit.util.NetworkGraph;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkLoadCaptor {
    public static final boolean debugDetail = true;
    public static final String CHANNEL_EVENT = "PK|ChEvent";
    public static final long NULL_CHUNK = 0x8000_800000_800000L;
    private static final ConcurrentMap<Thread, ChunkLoadSource> threadReasonCache = new ConcurrentHashMap<>();
    private static final Object2LongMap<Thread> threadChunkPosCache = Object2LongMaps.synchronize(new Object2LongOpenHashMap<>());

    static {
        threadChunkPosCache.defaultReturnValue(NULL_CHUNK);
    }

    private final NetworkGraph<ChunkPos, ChunkLoadReason> graph = new NetworkGraph<>(ChunkPos.class, ChunkLoadReason.class);

    public static void pushThreadSource(int chunkX, int chunkZ, DimensionType dimensionType, ChunkLoadReason reason) {
        ChunkLoadSource source = new ChunkLoadSource(chunkX, chunkZ, dimensionType, reason);
        pushThreadSource(source);
    }

    public static void pushThreadSource(ChunkLoadSource source) {
        Thread thread = Thread.currentThread();
        ChunkLoadSource previous = threadReasonCache.putIfAbsent(thread, source);
        if (previous != null) {
            throw new ConcurrentModificationException("Chunk loader thread " + thread + " is interrupted!");
        }
    }

    /**
     * Fetch and <b>remove</b>
     */
    public static ChunkLoadSource popThreadSource() {
        Thread thread = Thread.currentThread();
        return threadReasonCache.remove(thread);
    }

    public static void removeThreadSource() {
        Thread thread = Thread.currentThread();
        threadReasonCache.remove(thread);
    }

    public static long chunkToLong(int chunkX, int chunkZ, int dimOrdinal) {
        long code = (long) chunkX & 0xFFFFFFL;
        code |= ((long) chunkZ & 0xFFFFFFL) << 24;
        code |= ((long) dimOrdinal & 0xFFFFL) << 48;
        return code;
    }

    public static void debugOnChat(int tickCount, PhaseRecord record, ChunkPos currPos, @Nonnull DimensionType dimensionType, @Nonnull ChunkEvent event, ChunkLoadSource source, PlayerList playerList) {
        if (debugDetail) {
            return;
        }
        ITextComponent component = new TextComponentString(
                "[" + tickCount + ':' + record + ']'
        ).setStyle(new Style().setColor(TextFormatting.WHITE));
        StringBuilder msgBuilder = new StringBuilder(" Chunk ");
        msgBuilder.append(currPos).append(' ');
        switch (event) {
            case LOADING:
                msgBuilder.append("is loaded");
                break;
            case CANCEL_UNLOAD:
                msgBuilder.append("cancels unloading");
                break;
            case QUEUE_UNLOAD:
                msgBuilder.append("queues for unloading");
                break;
            case UNLOADING:
                msgBuilder.append("is unloaded");
                break;
            case GENERATING:
                msgBuilder.append("is generated");
                break;
            default:
                msgBuilder.append("has undefined behaviors");
        }
        ITextComponent body = new TextComponentString(msgBuilder.toString());
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
            ITextComponent tail = new TextComponentString(" from " + priorPos + " (" + reason + ')');
            tail.setStyle(new Style().setColor(TextFormatting.GRAY));
            body.appendSibling(tail);
        }
        component.appendSibling(body);
        playerList.sendMessage(component);
    }

    public static void debugChunkTickStamp(TickRecord record, int chunkX, int chunkZ, DimensionType dimensionType, ChunkEvent event, @SuppressWarnings("unused") ChunkLoadSource source, @Nonnull PlayerList playerList) {
        PotteckitPacketBuffer buffer = new PotteckitPacketBuffer(Unpooled.buffer());
        SPacketCustomPayload packet = new SPacketCustomPayload(CHANNEL_EVENT, buffer);
        buffer.writeTickRecord(record).writeSignedVarInt(chunkX).writeSignedVarInt(chunkZ);
        buffer.writeEnumValue(dimensionType).writeEnumValue(event);
        playerList.sendPacketToAllPlayers(packet);
    }
}
