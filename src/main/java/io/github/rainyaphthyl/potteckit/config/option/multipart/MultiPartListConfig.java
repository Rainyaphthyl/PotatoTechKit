package io.github.rainyaphthyl.potteckit.config.option.multipart;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;

import java.util.function.Function;

public class MultiPartListConfig<ENTRY extends MultiPartEntry<ENTRY>> extends ValueListConfig<ENTRY> {
    public MultiPartListConfig(String name, ImmutableList<ENTRY> defaultValues, Function<ENTRY, String> toStringConverter, Function<String, ENTRY> fromStringConverter, String commentTranslationKey, Object... commentArgs) {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }
}
