package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.ConfigBundle;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ConfigHandler {
    public static final List<ConfigOptionCategory> optionCategoryList = new ArrayList<>();
    public static final List<HotkeyCategory> hotkeyCategoryList = new ArrayList<>();
    public static final EnumMap<Domain, ConfigBundle> configBundleMap = new EnumMap<>(Domain.class);
    public static final List<Hotkey> allHotkeyList = new ArrayList<>();
    public static final List<ConfigTab> allTabList = new ArrayList<>();
    public static final ConfigTab defaultTab;

    static {
        for (Domain domain : Domain.values()) {
            ConfigBundle bundle = new ConfigBundle(domain.toString());
            configBundleMap.putIfAbsent(domain, bundle);
            optionCategoryList.add(bundle.optionCategory);
            hotkeyCategoryList.add(bundle.hotkeyCategory);
        }
        Field[] fieldArray = Configs.class.getFields();
        for (Field field : fieldArray) {
            Config annotation = field.getAnnotation(Config.class);
            try {
                Object value = field.get(null);
                ConfigOption<?> option = null;
                Hotkey hotkey = null;
                final boolean isOption = value instanceof ConfigOption;
                if (isOption) {
                    option = (ConfigOption<?>) value;
                }
                final boolean isHotkey = value instanceof Hotkey;
                if (isHotkey) {
                    hotkey = (Hotkey) value;
                    allHotkeyList.add(hotkey);
                }
                Domain[] domains = annotation.domains();
                for (Domain domain : domains) {
                    ConfigBundle bundle = configBundleMap.get(domain);
                    if (isOption) {
                        bundle.optionList.add(option);
                    }
                    if (isHotkey) {
                        bundle.hotkeyList.add(hotkey);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Domain domain : Domain.values()) {
            ConfigBundle bundle = configBundleMap.get(domain);
            BaseConfigTab tab = new BaseConfigTab(Reference.MOD_INFO, domain.key, 160, bundle.optionList, GuiConfigScreen::create);
            allTabList.add(tab);
        }
        defaultTab = allTabList.get(0);
    }
}
