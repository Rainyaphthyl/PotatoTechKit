package io.github.rainyaphthyl.potteckit.config.option.multipart;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import net.minecraft.world.DimensionType;

import javax.annotation.Nullable;

public class ChunkFilterEntry extends MultiPartEntry<ChunkFilterEntry> {
    public static final ChunkFilterEntry NULL_WHITE = new ChunkFilterEntry(true, DimensionType.OVERWORLD, GamePhase.CHUNK_UNLOAD, ChunkEvent.LOADING, DimensionType.OVERWORLD);

    protected ChunkFilterEntry(Class<?>[] typeArray, Object[] valueArray, boolean lazyCopy) {
        super(typeArray, valueArray, lazyCopy);
    }

    public ChunkFilterEntry(Boolean inverse, DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        this(inverse, timeDimension, gamePhase, chunkEvent, chunkDimension, 0);
    }

    public ChunkFilterEntry(Boolean inverse, DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension, Integer multiplicity) {
        super(
                new Class<?>[]{Boolean.class, DimensionType.class, GamePhase.class, ChunkEvent.class, DimensionType.class, Integer.class},
                new Object[]{inverse == null ? Boolean.FALSE : inverse,
                        timeDimension, gamePhase, chunkEvent, chunkDimension,
                        multiplicity == null ? 0 : multiplicity}
        );
    }
    // TODO: 2023/9/24,0024 Add field: "duration" or "depth", to reject a chunk multiple times after an event. E.g. Reject the following unloading after the unloading queueing, and even the next loading.

    public static ChunkFilterEntry fromObjectArray(Object... objects) {
        if (objects != null && objects.length >= 5
                && (objects[0] == null || objects[0] instanceof Boolean)
                && (objects[1] == null || objects[1] instanceof DimensionType)
                && (objects[2] == null || objects[2] instanceof GamePhase)
                && (objects[3] == null || objects[3] instanceof ChunkEvent)
                && (objects[4] == null || objects[4] instanceof DimensionType)
        ) {
            Integer multiplicity = 0;
            if (objects.length >= 6 && objects[5] instanceof Integer) {
                multiplicity = (Integer) objects[5];
            }
            return new ChunkFilterEntry((Boolean) objects[0], (DimensionType) objects[1], (GamePhase) objects[2], (ChunkEvent) objects[3], (DimensionType) objects[4], multiplicity);
        }
        return null;

    }

    public static ChunkFilterEntry fromString(String key) {
        if (key == null) {
            return null;
        }
        boolean inverse = key.startsWith("!");
        String mainKey = key.substring(key.lastIndexOf('!') + 1);
        String[] args = mainKey.split(":", -1);
        if (args.length != 5) {
            return null;
        }
        DimensionType timeDimension = fromDimensionString(args[0]);
        GamePhase gamePhase = GamePhase.fromShortName(args[1]);
        ChunkEvent chunkEvent = ChunkEvent.fromShortName(args[2]);
        DimensionType chunkDimension = fromDimensionString(args[3]);
        Integer multiplicity = Integer.parseInt(args[4]);
        return new ChunkFilterEntry(inverse, timeDimension, gamePhase, chunkEvent, chunkDimension, multiplicity);
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

    public int multiplicity() {
        return (Integer) getValue(5);
    }

    public boolean ignores(DimensionType timeDim, GamePhase gamePhase, ChunkEvent event, DimensionType posDim) {
        boolean matched = timeDimension() == null || timeDimension() == timeDim;
        if (matched) matched = gamePhase() == null || gamePhase() == gamePhase;
        if (matched) matched = chunkEvent() == null || chunkEvent() == event;
        if (matched) matched = chunkDimension() == null || chunkDimension() == posDim;
        return inverse() != matched;
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
        if (chunkDimension() != null) {
            builder.append(TickRecord.getDimensionChar(chunkDimension()));
        }
        builder.append(':').append(multiplicity());
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
            if (index >= 0 && index < valueArray.length && (newValues[i] == null || typeArray[index].isAssignableFrom(newValues[i].getClass()))) {
                args[index] = newValues[i];
            } else {
                return this;
            }
        }
        return new ChunkFilterEntry(typeArray, args, true);
    }
}
