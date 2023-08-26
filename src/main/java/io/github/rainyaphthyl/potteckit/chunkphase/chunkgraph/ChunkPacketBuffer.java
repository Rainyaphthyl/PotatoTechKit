package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.SubPhase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;

public class ChunkPacketBuffer extends PacketBuffer {
    public ChunkPacketBuffer(ByteBuf wrapped) {
        super(wrapped);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ChunkPacketBuffer writeTickRecord(TickRecord tickRecord) {
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
            writeSubPhase(tickRecord.subPhase);
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ChunkPacketBuffer writeSignedVarInt(int input) {
        int sign = (input & 0x80000000) >>> 31;
        long abs = Math.abs((long) input);
        long value = (abs << 1) | sign;
        writeVarLong(value);
        return this;
    }

    public int readSignedVarInt() {
        long value = readVarLong();
        int sign = (int) (value & 0x1L);
        long abs = value >>> 1;
        int output;
        if (sign == 0) {
            output = (int) abs;
        } else {
            output = (int) (-abs);
        }
        return output;
    }

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
        SubPhase subPhase = readSubPhase(gamePhase);
        return TickRecord.getInstance(tickOrdinal, gameTime, dimensionType, gamePhase, subPhase, eventOrdinal);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ChunkPacketBuffer writeSubPhase(SubPhase subPhase) {
        if (subPhase == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            subPhase.writeToPacket(this);
        }
        return this;
    }

    public SubPhase readSubPhase(GamePhase gamePhase) {
        if (readBoolean()) {
            Class<? extends SubPhase> subClass = gamePhase.subClass;
            if (subClass == null) return null;
            try {
                SubPhase subPhase = subClass.newInstance();
                subPhase.readFromPacket(this);
                return subPhase;
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
