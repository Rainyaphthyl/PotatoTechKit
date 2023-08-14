package io.github.rainyaphthyl.potteckit.config.annotation;

import java.util.EnumMap;

public class ConfigBundle {
    public static final EnumMap<Domain, ConfigBundle> configBundleMap = new EnumMap<>(Domain.class);

    static {
        for (Domain domain : Domain.values()) {
            configBundleMap.putIfAbsent(domain, new ConfigBundle());
        }
    }
}
