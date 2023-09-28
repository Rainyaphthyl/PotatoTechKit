package io.github.rainyaphthyl.potteckit.config.option.multipart;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ChunkFilterListConfig extends MultiPartListConfig<ChunkFilterEntry> {
    public ChunkFilterListConfig(String name, ImmutableList<ChunkFilterEntry> defaultValues, Function<ChunkFilterEntry, String> toStringConverter, Function<String, ChunkFilterEntry> fromStringConverter, String commentTranslationKey, Object... commentArgs) {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Nonnull
    public static ChunkFilterListConfig create(String cfgName, ImmutableList<ChunkFilterEntry> defaultList) {
        return new ChunkFilterListConfig(cfgName, defaultList, ChunkFilterEntry::toString, ChunkFilterEntry::fromString, cfgName);
    }
}
