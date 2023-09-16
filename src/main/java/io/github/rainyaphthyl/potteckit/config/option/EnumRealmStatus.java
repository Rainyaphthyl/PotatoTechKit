package io.github.rainyaphthyl.potteckit.config.option;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;

public enum EnumRealmStatus implements OptionListConfigValue {
    VANILLA("vanilla", "Vanilla"),
    DISABLED("disabled", "Disabled"),
    INVISIBLE("invisible", "Invisible");
    public static final ImmutableList<EnumRealmStatus> list = ImmutableList.copyOf(values());
    public final String name;
    public final String description;

    EnumRealmStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return description;
    }
}
