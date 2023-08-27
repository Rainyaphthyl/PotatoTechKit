package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public class BlockEventSubPhase extends SubPhase {
    private int depth;
    private int orderAtDepth;

    public BlockEventSubPhase(int depth, int orderAtDepth) {
        this.depth = depth;
        this.orderAtDepth = orderAtDepth;
    }

    @Override
    public void readFromPacket(@Nonnull PacketBuffer buffer) {
        depth = buffer.readVarInt();
        orderAtDepth = buffer.readVarInt();
    }

    @Override
    public void writeToPacket(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(getDepth());
        buffer.writeVarInt(getOrderAtDepth());
    }

    @Override
    public GamePhase parentPhase() {
        return GamePhase.BLOCK_EVENT;
    }

    @Override
    public int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException {
        if (obj == this) return 0;
        requiresSubClass(obj);
        BlockEventSubPhase that = (BlockEventSubPhase) obj;
        if (getDepth() != that.getDepth()) return Integer.compare(getDepth(), that.getDepth());
        return Integer.compare(getOrderAtDepth(), that.getOrderAtDepth());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockEventSubPhase)) return false;
        BlockEventSubPhase that = (BlockEventSubPhase) o;
        if (getDepth() != that.getDepth()) return false;
        return getOrderAtDepth() == that.getOrderAtDepth();
    }

    @Override
    public int hashCode() {
        int result = getDepth();
        result = 31 * result + getOrderAtDepth();
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(getDepth()) + ':' + getOrderAtDepth();
    }

    public int getDepth() {
        return depth;
    }

    public int getOrderAtDepth() {
        return orderAtDepth;
    }
}
