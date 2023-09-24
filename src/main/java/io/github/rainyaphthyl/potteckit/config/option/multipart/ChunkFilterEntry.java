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

    public ChunkFilterEntry(Boolean accepting, DimensionType timeDimension, GamePhase gamePhase, ChunkEvent chunkEvent, DimensionType chunkDimension) {
        super(
                new Class<?>[]{Boolean.class, DimensionType.class, GamePhase.class, ChunkEvent.class, DimensionType.class},
                new Object[]{accepting == null ? Boolean.FALSE : accepting, timeDimension, gamePhase, chunkEvent, chunkDimension}
        );
    }
    // TODO: 2023/9/24,0024 Add field: "duration" or "depth", to reject a chunk multiple times after an event. E.g. Reject the following unloading after the unloading queueing.

    public static ChunkFilterEntry fromObjectArray(Object... objects) {
        if (objects != null && objects.length == 5
                && objects[0] instanceof Boolean
                && objects[1] instanceof DimensionType
                && objects[2] instanceof GamePhase
                && objects[3] instanceof ChunkEvent
                && objects[4] instanceof DimensionType
        ) {
            return new ChunkFilterEntry(
                    (Boolean) objects[0], (DimensionType) objects[1], (GamePhase) objects[2],
                    (ChunkEvent) objects[3], (DimensionType) objects[4]
            );
        } else {
            return null;
        }
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

    public boolean rejecting() {
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

    public boolean reject(DimensionType timeDim, GamePhase gamePhase, ChunkEvent event, DimensionType posDim) {
        boolean matched = timeDimension() == null || timeDimension() == timeDim;
        if (matched) matched = gamePhase() == null || gamePhase() == gamePhase;
        if (matched) matched = chunkEvent() == null || chunkEvent() == event;
        if (matched) matched = chunkDimension() == null || chunkDimension() == posDim;
        return rejecting() == matched;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (rejecting()) {
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
            if (index >= 0 && index < valueArray.length && (newValues[i] == null || typeArray[index].isAssignableFrom(newValues[i].getClass()))) {
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
