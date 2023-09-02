package io.github.rainyaphthyl.potteckit.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class ImmutableSetView<T> implements Set<T> {
    private final Collection<? super T> parent;
    private final Class<? extends T> elemClass;

    ImmutableSetView(Collection<? super T> parent, Class<? extends T> elemClass) {
        this.parent = Objects.requireNonNull(parent);
        this.elemClass = Objects.requireNonNull(elemClass);
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
    @Nonnull
    public Iterator<T> iterator() {
        return new IteratorView();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return parent.toArray(new Object[0]);
    }

    @Override
    @Nonnull
    public <S> S[] toArray(@Nonnull S[] a) {
        return parent.toArray(a);
    }

    @Override
    @Deprecated
    public boolean add(T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return parent.containsAll(c);
    }

    @Override
    @Deprecated
    public boolean addAll(@Nonnull Collection<? extends T> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean retainAll(@Nonnull Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public boolean removeAll(@Nonnull Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    @Override
    @Deprecated
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
    }

    public class IteratorView implements Iterator<T> {
        private final Iterator<? super T> objectIterator;

        private IteratorView() {
            objectIterator = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return objectIterator.hasNext();
        }

        @Override
        public T next() {
            Object nextElem = objectIterator.next();
            return elemClass.cast(nextElem);
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException(ImmutableSetView.class + " " + ImmutableSetView.this + " is immutable.");
        }
    }
}
