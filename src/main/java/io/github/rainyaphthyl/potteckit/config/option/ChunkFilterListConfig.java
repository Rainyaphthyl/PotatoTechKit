package io.github.rainyaphthyl.potteckit.config.option;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ChunkFilterListConfig extends ValueListConfig<ChunkFilterEntry> {
    public ChunkFilterListConfig(String name, ImmutableList<ChunkFilterEntry> defaultValues, Function<ChunkFilterEntry, String> toStringConverter, Function<String, ChunkFilterEntry> fromStringConverter, String commentTranslationKey, Object... commentArgs) {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Nonnull
    public static ChunkFilterListConfig create(String cfgName, ImmutableList<ChunkFilterEntry> defaultList) {
        return new ChunkFilterListConfig(cfgName, defaultList, ChunkFilterEntry::toString, ChunkFilterEntry::fromString, cfgName);
    }
}
