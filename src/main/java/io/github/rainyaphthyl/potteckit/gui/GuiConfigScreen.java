package io.github.rainyaphthyl.potteckit.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.config.Reference;

import javax.annotation.Nonnull;
import java.util.List;

public class GuiConfigScreen {
    public static final BaseConfigTab GENERIC;
    public static final BaseConfigTab ACTIONS;
    public static final List<ConfigTab> ALL_TABS;

    static {
        GENERIC = new BaseConfigTab(Reference.MOD_INFO, "generic", 160, Configs.GENERIC.OPTION_LIST, GuiConfigScreen::create);
        ACTIONS = new BaseConfigTab(Reference.MOD_INFO, "actions", 160, Configs.ACTIONS.OPTION_LIST, GuiConfigScreen::create);
        ALL_TABS = ImmutableList.of(GENERIC, ACTIONS);
    }

    @Nonnull
    public static BaseConfigScreen create() {
        return new BaseConfigScreen(Reference.MOD_INFO, ALL_TABS, GENERIC, "potteckit.title.configs", Reference.VERSION);
    }

    public static List<ConfigTab> getConfigTabs() {
        return ALL_TABS;
    }
}
