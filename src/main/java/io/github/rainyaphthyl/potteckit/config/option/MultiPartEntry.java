package io.github.rainyaphthyl.potteckit.config.option;

import java.util.Objects;

public abstract class MultiPartEntry<ENTRY extends MultiPartEntry<ENTRY>> {
    protected final Class<?>[] typeArray;
    protected final Object[] valueArray;

    protected MultiPartEntry(Class<?>[] typeArray, Object[] valueArray) {
        this(typeArray, valueArray, false);
    }

    protected MultiPartEntry(Class<?>[] typeArray, Object[] valueArray, boolean lazyCopy) {
        Objects.requireNonNull(typeArray);
        Objects.requireNonNull(valueArray);
        if (typeArray.length != valueArray.length) throw new IllegalArgumentException();
        if (lazyCopy) {
            this.typeArray = typeArray;
            this.valueArray = valueArray;
        } else {
            this.typeArray = new Class<?>[typeArray.length];
            System.arraycopy(typeArray, 0, this.typeArray, 0, typeArray.length);
            this.valueArray = new Object[valueArray.length];
            System.arraycopy(valueArray, 0, this.valueArray, 0, valueArray.length);
        }
    }

    public Class<?> getType(int index) {
        return typeArray[index];
    }

    public Object getValue(int index) {
        return valueArray[index];
    }

    public ENTRY copyModified(int index, Object newValue) {
        return copyModified(new int[]{index}, newValue);
    }

    public abstract ENTRY copyModified(int[] indices, Object... newValues);
    //{
    //    if (indices == null || newValues == null || indices.length != newValues.length) {
    //        return  this;
    //    }
    //    Object[] args = new Object[valueArray.length];
    //    System.arraycopy(valueArray, 0, args, 0, valueArray.length);
    //    for (int i = 0; i < indices.length; ++i) {
    //        int index = indices[i];
    //        if (index >= 0 && index < valueArray.length && typeArray[index].isAssignableFrom(newValues[i].getClass())) {
    //            args[index] = newValues[i];
    //        } else {
    //            return this;
    //        }
    //    }
    //    return new MultiPartEntry(typeArray, args, true);
    //}
}
