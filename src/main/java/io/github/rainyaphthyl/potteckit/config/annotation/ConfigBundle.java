package io.github.rainyaphthyl.potteckit.config.annotation;

import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import io.github.rainyaphthyl.potteckit.config.Reference;

import java.util.ArrayList;
import java.util.List;

public class ConfigBundle {
    public final ConfigOptionCategory optionCategory;
    public final HotkeyCategory hotkeyCategory;
    public final List<ConfigOption<?>> optionList = new ArrayList<>();
    public final List<Hotkey> hotkeyList = new ArrayList<>();

    public ConfigBundle(String name) {
        optionCategory = BaseConfigOptionCategory.normal(Reference.MOD_INFO, name, optionList);
        hotkeyCategory = new HotkeyCategory(Reference.MOD_INFO, name, hotkeyList);
    }
}
