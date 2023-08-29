package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkPacketBuffer;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * All inheritors to this class must have a constructor without arguments
 */
public abstract class SubPhase implements Comparable<SubPhase> {
    protected static final int FIELD_MASK = Modifier.FINAL | Modifier.STATIC;
    protected static final int FIELD_EXPECTATION = Modifier.FINAL;

    /**
     * All inheritors to this class must have a constructor without arguments
     */
    public SubPhase() {
    }

    public static SubPhase createInstance(GamePhase gamePhase) {
        if (gamePhase == null) {
            return null;
        }
        Class<? extends SubPhase> subClass = gamePhase.subClass;
        if (subClass == null) {
            return null;
        }
        try {
            switch (gamePhase) {
                case BLOCK_EVENT:
                    return subClass.getConstructor(int.class, int.class).newInstance(0, 0);
                case TILE_TICK:
                    return subClass.getConstructor(long.class, int.class, long.class).newInstance(0L, 0, 0L);
                default:
                    return null;
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    public abstract GamePhase parentPhase();

    /**
     * @param obj the object to be compared.
     * @return the comparison result
     * @throws UnsupportedOperationException if {@code obj} is not an instance of this class
     */
    @Override
    public abstract int compareTo(@Nonnull SubPhase obj) throws UnsupportedOperationException;

    protected void requiresSubClass(SubPhase obj) throws UnsupportedOperationException {
        try {
            Class<? extends SubPhase> aClass = getClass();
            Class<? extends SubPhase> bClass = Objects.requireNonNull(obj).getClass();
            if (!aClass.isAssignableFrom(bClass)) throw new ClassCastException();
        } catch (NullPointerException | ClassCastException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public abstract void readFromPacket(@Nonnull ChunkPacketBuffer buffer);

    public abstract void writeToPacket(@Nonnull ChunkPacketBuffer buffer);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Class<? extends SubPhase> subClass = getClass();
        Field[] fields = subClass.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            int modifiers = field.getModifiers();
            if ((modifiers & FIELD_MASK) == FIELD_EXPECTATION) {
                try {
                    boolean access = field.isAccessible();
                    field.setAccessible(true);
                    Object object = field.get(this);
                    field.setAccessible(access);
                    if (i > 0) {
                        builder.append(':');
                    }
                    if (object != null) {
                        builder.append(object);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return builder.toString();
    }
}
