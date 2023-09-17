package io.github.rainyaphthyl.potteckit.input;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.SubPhase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;

public class PotteckitPacketBuffer extends PacketBuffer {
    public PotteckitPacketBuffer(ByteBuf wrapped) {
        super(wrapped);
    }

    public PotteckitPacketBuffer writeTickRecord(TickRecord tickRecord) {
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

    public PotteckitPacketBuffer writeSignedVarInt(int input) {
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
    public PotteckitPacketBuffer writeSubPhase(SubPhase subPhase) {
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
            SubPhase subPhase = SubPhase.createInstance(gamePhase);
            if (subPhase != null) {
                subPhase.readFromPacket(this);
            }
            return subPhase;
        } else {
            return null;
        }
    }
}
