package io.github.rainyaphthyl.potteckit.config.option.multipart;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NullableEnum<E extends Enum<E>> extends WrappedValue<E> implements Comparable<NullableEnum<E>> {
    public NullableEnum(@Nullable E value) {
        super(value);
    }

    public static <E extends Enum<E>> ImmutableList<? extends NullableEnum<E>> getListOfType(@Nonnull Class<E> enumClass) {
        ImmutableList.Builder<NullableEnum<E>> builder = ImmutableList.builder();
        builder.add(new NullableEnum<>(null));
        E[] constants = enumClass.getEnumConstants();
        for (E constant : constants) {
            builder.add(new NullableEnum<>(constant));
        }
        return builder.build();
    }

    public static <E extends Enum<E>> ImmutableList<PartialValue<Object>> getObjectListOfType(@Nonnull Class<E> enumClass) {
        ImmutableList.Builder<PartialValue<Object>> builder = ImmutableList.builder();
        builder.add(new WrappedValue<>(null));
        E[] constants = enumClass.getEnumConstants();
        for (E constant : constants) {
            builder.add(new WrappedValue<>(constant));
        }
        return builder.build();
    }

    @Override
    public int compareTo(@Nonnull NullableEnum<E> o) {
        E value = getValue();
        E other = o.getValue();
        if (value == other) return 0;
        if (value == null) return -1;
        if (other == null) return 1;
        return value.compareTo(other);
    }
}
