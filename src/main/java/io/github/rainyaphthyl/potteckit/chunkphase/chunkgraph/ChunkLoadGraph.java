package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import com.google.common.collect.ImmutableList;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterEntry;
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
                for (ChunkFilterEntry filter : filterList) {
                    if (filter != null) {
                        boolean flag = filter.timeDimension() == null || filter.timeDimension() == tickRecord.dimensionType;
                        if (flag) flag = filter.gamePhase() == null || filter.gamePhase() == tickRecord.gamePhase;
                        if (flag) flag = filter.chunkEvent() == null || filter.chunkEvent() == event;
                        if (flag) flag = filter.chunkDimension() == null || filter.chunkDimension() == targetDim;
                        if (filter.inverse() == flag) {
                            return;
                        }
                    }
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
}
