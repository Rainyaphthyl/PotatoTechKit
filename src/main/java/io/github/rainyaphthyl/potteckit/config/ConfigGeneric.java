package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;

public class ConfigGeneric extends AbstractPartialConfig {
    public final HotkeyedBooleanConfig GLOBAL_SWITCH = new HotkeyedBooleanConfig("globalSwitch", true, "");

    protected ConfigGeneric() {
        super("Generic");
        initAllList();
    }
}
