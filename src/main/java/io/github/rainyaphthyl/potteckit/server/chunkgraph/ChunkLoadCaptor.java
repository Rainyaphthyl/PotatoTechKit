package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.server.phaseclock.PhaseRecord;
import io.github.rainyaphthyl.potteckit.server.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.util.NetworkGraph;
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
    private static final ConcurrentMap<Thread, ChunkLoadSource> threadReasonCache = new ConcurrentHashMap<>();
    private final NetworkGraph<ChunkPos, ChunkLoadReason> graph = new NetworkGraph<>(ChunkPos.class, ChunkLoadReason.class);

    public static void pushThreadSource(int chunkX, int chunkZ, ChunkLoadReason reason) {
        ChunkLoadSource source = new ChunkLoadSource(chunkX, chunkZ, reason);
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

    public static void debugChunkTickStamp(TickRecord record, ChunkPos currPos, @Nonnull DimensionType dimensionType, @Nonnull ChunkEvent event, ChunkLoadSource source, PlayerList playerList) {
        ITextComponent component = new TextComponentString(String.valueOf(record));
        component = component.setStyle(new Style().setColor(TextFormatting.WHITE));
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
        component.appendSibling(body);
        playerList.sendMessage(component);
    }
}
