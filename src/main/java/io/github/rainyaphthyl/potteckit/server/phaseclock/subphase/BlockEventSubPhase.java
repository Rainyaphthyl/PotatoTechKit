package io.github.rainyaphthyl.potteckit.server.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
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
        buffer.writeVarInt(depth);
        buffer.writeVarInt(orderAtDepth);
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
        if (depth != that.depth) return Integer.compare(depth, that.depth);
        return Integer.compare(orderAtDepth, that.orderAtDepth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockEventSubPhase)) return false;
        BlockEventSubPhase that = (BlockEventSubPhase) o;
        if (depth != that.depth) return false;
        return orderAtDepth == that.orderAtDepth;
    }

    @Override
    public int hashCode() {
        int result = depth;
        result = 31 * result + orderAtDepth;
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(depth) + ':' + orderAtDepth;
    }
}
