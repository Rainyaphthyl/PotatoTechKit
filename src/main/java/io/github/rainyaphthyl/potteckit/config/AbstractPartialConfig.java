package io.github.rainyaphthyl.potteckit.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.input.Hotkey;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public abstract class AbstractPartialConfig {
    private static final Map<Class<? extends AbstractPartialConfig>, AbstractPartialConfig> instanceCache = Maps.newHashMap();
    public final String NAME;
    public final List<ConfigOption<?>> OPTION_LIST = Lists.newArrayList();
    public final List<Hotkey> HOTKEY_LIST = Lists.newArrayList();

    protected AbstractPartialConfig(String name) {
        NAME = name;
    }

    public static <T extends AbstractPartialConfig> T getInstance(@Nonnull Class<T> configClass) {
        AbstractPartialConfig instance = instanceCache.get(configClass);
        if (instance == null || !configClass.isAssignableFrom(instance.getClass())) {
            try {
                instance = configClass.getDeclaredConstructor().newInstance();
                instanceCache.put(configClass, instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                instance = null;
                Reference.LOGGER.error(e);
            }
        }
        return configClass.cast(instance);
    }

    protected void initAllList() {
        Field[] fieldArray = getClass().getDeclaredFields();
        for (Field field : fieldArray) {
            Class<?> fieldType = field.getType();
            if (ConfigOption.class.isAssignableFrom(fieldType)) {
                try {
                    Object configObj = field.get(this);
                    if (configObj instanceof ConfigOption) {
                        OPTION_LIST.add((ConfigOption<?>) configObj);
                    }
                } catch (IllegalAccessException e) {
                    Reference.LOGGER.error(e);
                }
            }
            if (Hotkey.class.isAssignableFrom(fieldType)) {
                try {
                    Object hotkeyObj = field.get(this);
                    if (hotkeyObj instanceof Hotkey) {
                        HOTKEY_LIST.add((Hotkey) hotkeyObj);
                    }
                } catch (IllegalAccessException e) {
                    Reference.LOGGER.error(e);
                }
            }
        }
    }
}
