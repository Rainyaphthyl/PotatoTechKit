package io.github.rainyaphthyl.potteckit.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class ImmutableSetView<T> implements Set<T> {
    private final Collection<? super T> parent;
    private final Class<? extends T> targetClass;

    ImmutableSetView(Collection<? super T> parent, Class<? extends T> targetClass) {
        this.parent = Objects.requireNonNull(parent);
        this.targetClass = Objects.requireNonNull(targetClass);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return parent.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<? super T> objectIterator = parent.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return objectIterator.hasNext();
            }

            @Override
            public T next() {
                Object nextElem = objectIterator.next();
                return targetClass.cast(nextElem);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return parent.toArray(new Object[0]);
    }

    @Override
    public <S> S[] toArray(@Nonnull S[] a) {
        return parent.toArray(a);
    }

    @Override
    @Deprecated
    public boolean add(T t) {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return parent.containsAll(c);
    }

    @Override
    @Deprecated
    public boolean addAll(@Nonnull Collection<? extends T> c) {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean retainAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean removeAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }
}
