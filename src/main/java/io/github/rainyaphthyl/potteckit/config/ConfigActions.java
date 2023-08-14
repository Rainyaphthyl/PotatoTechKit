package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.option.HotkeyConfig;

public class ConfigActions extends AbstractPartialConfig {
    public final HotkeyConfig OPEN_CONFIG_PANEL = new HotkeyConfig("openConfigScreen", "K,C");

    protected ConfigActions() {
        super("Actions");
        initAllList();
    }
}
