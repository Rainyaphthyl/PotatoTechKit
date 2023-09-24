package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import com.google.common.collect.ImmutableList;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.config.option.multipart.ChunkFilterEntry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;

public class ChunkLoadGraph {
    public static final Object2IntMap<DimChunkPos> chunkRepeatCache = new Object2IntOpenHashMap<>();

    static {
        chunkRepeatCache.defaultReturnValue(0);
    }

    public static void receiveChunkEventPacket(@Nonnull SPacketCustomPayload packetIn) {
        if (Configs.chunkLoadingGraphReceiver.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            PacketBuffer rawBuffer = packetIn.getBufferData();
            ChunkPacketBuffer buffer = rawBuffer instanceof ChunkPacketBuffer ? (ChunkPacketBuffer) rawBuffer : new ChunkPacketBuffer(rawBuffer);
            TickRecord tickRecord = buffer.readTickRecord();
            int targetCX = buffer.readSignedVarInt();
            int targetCZ = buffer.readSignedVarInt();
            DimensionType targetDim = buffer.readEnumValue(DimensionType.class);
            ChunkEvent event = buffer.readEnumValue(ChunkEvent.class);
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> {
                ImmutableList<ChunkFilterEntry> filterList = Configs.chunkLoadFilterList.getValue();
                DimChunkPos pos = new DimChunkPos(targetDim, targetCX, targetCZ);
                int repeatCount = chunkRepeatCache.getInt(pos);
                boolean ignored = repeatCount > 0;
                if (ignored) {
                    --repeatCount;
                    if (repeatCount > 0) {
                        chunkRepeatCache.put(pos, repeatCount);
                    } else {
                        chunkRepeatCache.removeInt(pos);
                    }
                }
                for (ChunkFilterEntry filter : filterList) {
                    if (filter != null && filter.ignores(tickRecord.dimensionType, tickRecord.gamePhase, event, targetDim)) {
                        int repeatPending = filter.multiplicity();
                        if (repeatPending > 0 && repeatPending > chunkRepeatCache.getInt(pos)) {
                            chunkRepeatCache.put(pos, repeatPending);
                        }
                        ignored = true;
                    }
                }
                if (ignored) {
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append(tickRecord).append(' ');
                ITextComponent component = new TextComponentString(builder.toString());
                component.setStyle(new Style().setColor(TextFormatting.GRAY));
                builder.setLength(0);
                builder.append(TickRecord.getDimensionChar(targetDim));
                builder.append(":[").append(targetCX).append(", ").append(targetCZ).append("]");
                ITextComponent appendix = new TextComponentString(builder.toString());
                appendix.setStyle(new Style().setColor(TextFormatting.WHITE));
                component.appendSibling(appendix);
                builder.setLength(0);
                builder.append(' ').append(event.description);
                appendix = new TextComponentString(builder.toString());
                appendix.setStyle(new Style().setColor(event.color));
                component.appendSibling(appendix);
                NetHandlerPlayClient connection = client.getConnection();
                if (connection != null) {
                    connection.handleChat(new SPacketChat(component));
                }
            });
        }
    }

    public static class DimChunkPos {
        public final DimensionType dimensionType;
        public final int chunkX;
        public final int chunkZ;

        public DimChunkPos(DimensionType dimensionType, int chunkX, int chunkZ) {
            this.dimensionType = dimensionType;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof DimChunkPos)) return false;
            DimChunkPos that = (DimChunkPos) object;
            if (chunkX != that.chunkX) return false;
            if (chunkZ != that.chunkZ) return false;
            return dimensionType == that.dimensionType;
        }

        @Override
        public int hashCode() {
            int result = dimensionType != null ? dimensionType.hashCode() : 0;
            result = 31 * result + chunkX;
            result = 31 * result + chunkZ;
            return result;
        }

        @Override
        public String toString() {
            return "[" + TickRecord.getDimensionChar(dimensionType) + ": " + chunkX + ", " + chunkZ + ']';
        }
    }
}
