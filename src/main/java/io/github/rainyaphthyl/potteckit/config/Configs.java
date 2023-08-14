package io.github.rainyaphthyl.potteckit.config;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.config.annotation.Type;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.input.PotteckitHotkeyProvider;

import java.lang.reflect.Field;
import java.util.List;

public class Configs {
    public static final GenericConfigs GENERIC = AbstractPartialConfig.getInstance(GenericConfigs.class);
    public static final ActionConfigs ACTIONS = AbstractPartialConfig.getInstance(ActionConfigs.class);
    public static final List<ConfigOptionCategory> CATEGORIES = Lists.newArrayList();
    public static final List<Hotkey> ALL_HOTKEYS = Lists.newArrayList();
    public static final List<HotkeyCategory> HOTKEY_CATEGORY_LIST = Lists.newArrayList();
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enablePotteckit", true, "");
    @Config(types = Type.HOTKEY, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("openConfigScreen", "K,C");

    static {
        Field[] fieldArray = Configs.class.getFields();
        for (Field field : fieldArray) {
            Config annotation = field.getAnnotation(Config.class);
            Domain[] domains = annotation.domains();
            for (Domain domain : domains) {
                switch (domain) {
                    case GENERIC:
                }
            }
            Class<?> fieldType = field.getType();
            if (AbstractPartialConfig.class.isAssignableFrom(fieldType)) {
                try {
                    Object obj = field.get(null);
                    if (obj instanceof AbstractPartialConfig) {
                        AbstractPartialConfig config = (AbstractPartialConfig) obj;
                        CATEGORIES.add(BaseConfigOptionCategory.normal(Reference.MOD_INFO, config.NAME, config.OPTION_LIST));
                        ALL_HOTKEYS.addAll(config.HOTKEY_LIST);
                        HOTKEY_CATEGORY_LIST.add(new HotkeyCategory(Reference.MOD_INFO, config.NAME, config.HOTKEY_LIST));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void registerOnInit() {
        JsonModConfig jsonModConfig = new JsonModConfig(Reference.MOD_INFO, Reference.CONFIG_VERSION, CATEGORIES);
        Registry.CONFIG_MANAGER.registerConfigHandler(jsonModConfig);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, GuiConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, GuiConfigScreen::getConfigTabs);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new PotteckitHotkeyProvider());
        Actions.init();
        Callbacks.init();
    }

    public static class ActionConfigs extends AbstractPartialConfig {
        public final HotkeyConfig OPEN_CONFIG_PANEL = new HotkeyConfig("openConfigScreen", "K,C");

        protected ActionConfigs() {
            super("Actions");
            initAllList();
        }
    }

    public static class GenericConfigs extends AbstractPartialConfig {
        public final HotkeyedBooleanConfig GLOBAL_SWITCH = new HotkeyedBooleanConfig("enablePotteckit", true, "");

        protected GenericConfigs() {
            super("Generic");
            initAllList();
        }
    }
}
