package io.github.rainyaphthyl.potteckit.config.option.multipart;

import javax.annotation.Nullable;

public class WrappedValue<E> implements PartialValue<E> {
    private final E value;

    public WrappedValue(@Nullable E value) {
        this.value = value;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WrappedValue)) return false;
        WrappedValue<?> that = (WrappedValue<?>) object;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public final int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public E getValue() {
        return value;
    }
}
