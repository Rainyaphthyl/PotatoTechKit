package io.github.rainyaphthyl.potteckit.config.option.multipart;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import net.minecraft.world.DimensionType;

import javax.annotation.Nullable;

public class ChunkFilterEntry extends MultiPartEntry<ChunkFilterEntry> {
    public static final ChunkFilterEntry NULL_WHITE = new ChunkFilterEntry(DimensionType.OVERWORLD, GamePhase.CHUNK_UNLOAD, ChunkEvent.LOADING, DimensionType.OVERWORLD);

    protected ChunkFilterEntry(Class<?>[] typeArray, Object[] valueArray, boolean lazyCopy) {
        super(typeArray, valueArray, lazyCopy);
    }

    public ChunkFilterEntry(DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        this(false, timeDimension, gamePhase, chunkEvent, chunkDimension);
    }

    public ChunkFilterEntry(Boolean inverse, DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        super(
                new Class<?>[]{Boolean.class, DimensionType.class, GamePhase.class, ChunkEvent.class, DimensionType.class},
                new Object[]{inverse == null ? Boolean.FALSE : inverse, timeDimension, gamePhase, chunkEvent, chunkDimension}
        );
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

    public boolean inverse() {
        return (Boolean) getValue(0);
    }

    public DimensionType timeDimension() {
        return (DimensionType) getValue(1);
    }

    public GamePhase gamePhase() {
        return (GamePhase) getValue(2);
    }

    public ChunkEvent chunkEvent() {
        return (ChunkEvent) getValue(3);
    }

    public DimensionType chunkDimension() {
        return (DimensionType) getValue(4);
    }

    public boolean accept(DimensionType timeDim, GamePhase gamePhase, ChunkEvent event, DimensionType posDim) {
        boolean matched = timeDimension() == null || timeDimension() == timeDim;
        if (matched) matched = gamePhase() == null || gamePhase() == gamePhase;
        if (matched) matched = chunkEvent() == null || chunkEvent() == event;
        if (matched) matched = chunkDimension() == null || chunkDimension() == posDim;
        return inverse() == matched;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (inverse()) {
            builder.append('!');
        }
        if (timeDimension() != null) {
            builder.append(TickRecord.getDimensionChar(timeDimension()));
        }
        builder.append(':');
        if (gamePhase() != null) {
            builder.append(gamePhase().shortName);
        }
        builder.append(':');
        if (chunkEvent() != null) {
            builder.append(chunkEvent().shortName);
        }
        builder.append(':');
        if (timeDimension() != null) {
            builder.append(TickRecord.getDimensionChar(timeDimension()));
        }
        return builder.toString();
    }

    @Override
    public ChunkFilterEntry copyModified(int[] indices, Object... newValues) {
        if (indices == null || newValues == null || indices.length != newValues.length) {
            return this;
        }
        Object[] args = new Object[valueArray.length];
        System.arraycopy(valueArray, 0, args, 0, valueArray.length);
        for (int i = 0; i < indices.length; ++i) {
            int index = indices[i];
            if (index >= 0 && index < valueArray.length && typeArray[index].isAssignableFrom(newValues[i].getClass())) {
                args[index] = newValues[i];
            } else {
                return this;
            }
        }
        return new ChunkFilterEntry(typeArray, args, true);
    }

    public enum Action {
        PASS,
        ACCEPT,
        REJECT
    }
}
