package io.github.rainyaphthyl.potteckit.config.option;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import net.minecraft.world.DimensionType;

import javax.annotation.Nullable;

public class ChunkFilterEntry {
    public final boolean inverse;
    public final DimensionType timeDimension;
    public final GamePhase gamePhase;
    public final ChunkEvent chunkEvent;
    public final DimensionType chunkDimension;

    public ChunkFilterEntry(DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        this(false, timeDimension, gamePhase, chunkEvent, chunkDimension);
    }

    public ChunkFilterEntry(boolean inverse, DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        this.inverse = inverse;
        this.gamePhase = gamePhase;
        this.timeDimension = timeDimension;
        this.chunkEvent = chunkEvent;
        this.chunkDimension = chunkDimension;
    }

    public static ChunkFilterEntry fromString(String key) {
        if (key == null) {
            return null;
        }
        boolean inverse = key.startsWith("!");
        String mainKey = key.substring(key.lastIndexOf('!') + 1);
        String[] args = mainKey.split("^:$", -1);
        if (args.length != 4) {
            return null;
        }
        DimensionType timeDimension = fromDimensionString(args[0]);
        GamePhase gamePhase = GamePhase.fromShortName(args[1]);
        ChunkEvent chunkEvent = ChunkEvent.fromShortName(args[2]);
        DimensionType chunkDimension = fromDimensionString(args[3]);
        return new ChunkFilterEntry(inverse, timeDimension, gamePhase, chunkEvent, chunkDimension);
    }

    @Nullable
    public static DimensionType fromDimensionChar(char code) {
        switch (code) {
            case 'w':
                return DimensionType.OVERWORLD;
            case 'n':
                return DimensionType.NETHER;
            case 'e':
                return DimensionType.THE_END;
            default:
                return null;
        }
    }

    public static DimensionType fromDimensionString(String code) {
        if (code == null || code.length() != 1) {
            return null;
        } else {
            return fromDimensionChar(code.charAt(0));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (inverse) {
            builder.append('!');
        }
        if (timeDimension != null) {
            builder.append(TickRecord.getDimensionChar(timeDimension));
        }
        builder.append(':');
        if (gamePhase != null) {
            builder.append(gamePhase.shortName);
        }
        builder.append(':');
        if (chunkEvent != null) {
            builder.append(chunkEvent.shortName);
        }
        builder.append(':');
        if (timeDimension != null) {
            builder.append(TickRecord.getDimensionChar(timeDimension));
        }
        return builder.toString();
    }
}
