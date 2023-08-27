package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import io.github.rainyaphthyl.potteckit.config.annotation.Category;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.ConfigBundle;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.util.Reference;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ConfigHandler {
    public static final List<ConfigOptionCategory> optionCategoryList = new ArrayList<>();
    public static final List<HotkeyCategory> hotkeyCategoryList = new ArrayList<>();
    public static final EnumMap<Category, ConfigBundle> configBundleMap = new EnumMap<>(Category.class);
    public static final List<Hotkey> allHotkeyList = new ArrayList<>();
    public static final List<ConfigTab> allTabList = new ArrayList<>();
    public static final ConfigTab defaultTab;

    static {
        for (Category category : Category.values()) {
            ConfigBundle bundle = new ConfigBundle(category.name);
            configBundleMap.putIfAbsent(category, bundle);
            optionCategoryList.add(bundle.optionCategory);
            hotkeyCategoryList.add(bundle.hotkeyCategory);
        }
        Field[] fieldArray = Configs.class.getFields();
        for (Field field : fieldArray) {
            Config annotation = field.getAnnotation(Config.class);
            if (annotation != null) {
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
                    Category[] categories = getCategories(annotation);
                    for (Category category : categories) {
                        ConfigBundle bundle = configBundleMap.get(category);
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
        }
        for (Category category : Category.values()) {
            ConfigBundle bundle = configBundleMap.get(category);
            BaseConfigTab tab = new BaseConfigTab(Reference.MOD_INFO, category.key, 160, bundle.optionList, GuiConfigScreen::create);
            allTabList.add(tab);
        }
        defaultTab = allTabList.get(0);
    }

    @Nonnull
    private static Category[] getCategories(@Nonnull Config annotation) {
        Domain[] domains = annotation.domains();
        List<Category> categoryList = new ArrayList<>();
        for (Domain domain : domains) {
            categoryList.add(domain.category);
        }
        if (annotation.cheating()) categoryList.add(Category.CHEATING);
        if (annotation.notVanilla()) categoryList.add(Category.NOT_VANILLA);
        if (annotation.serverSide()) categoryList.add(Category.WITH_SERVER);
        return categoryList.toArray(new Category[0]);
    }
}
