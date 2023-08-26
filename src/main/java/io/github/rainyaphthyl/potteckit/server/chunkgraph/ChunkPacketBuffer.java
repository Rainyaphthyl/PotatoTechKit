package io.github.rainyaphthyl.potteckit.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.TickRecord;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;

import javax.annotation.Nullable;

public class ChunkPacketBuffer extends PacketBuffer {
    public ChunkPacketBuffer(ByteBuf wrapped) {
        super(wrapped);
    }

    public ChunkPacketBuffer writeTickRecord(@Nullable TickRecord tickRecord) {
        if (tickRecord == null) {
            writeVarInt(-1);
        } else {
            writeVarInt(tickRecord.eventOrdinal);
            writeVarInt(tickRecord.tickOrdinal);
            writeVarLong(tickRecord.gameTime);
            GamePhase gamePhase = tickRecord.gamePhase;
            writeEnumValue(gamePhase);
            if (gamePhase.dimensional) {
                writeEnumValue(tickRecord.dimensionType);
            }
        }
        return this;
    }

    @Nullable
    public TickRecord readTickRecord() {
        int eventOrdinal = readVarInt();
        if (eventOrdinal == -1) {
            return null;
        }
        int tickOrdinal = readVarInt();
        long gameTime = readVarLong();
        GamePhase gamePhase = readEnumValue(GamePhase.class);
        DimensionType dimensionType = null;
        if (gamePhase.dimensional) {
            dimensionType = readEnumValue(DimensionType.class);
        }
        return TickRecord.getInstance(tickOrdinal, gameTime, dimensionType, gamePhase, null, eventOrdinal);
    }
}
