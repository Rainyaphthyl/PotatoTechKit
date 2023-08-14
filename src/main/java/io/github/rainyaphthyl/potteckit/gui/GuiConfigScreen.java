package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import io.github.rainyaphthyl.potteckit.config.ConfigHandler;
import io.github.rainyaphthyl.potteckit.config.Reference;

import javax.annotation.Nonnull;
import java.util.List;

public class GuiConfigScreen {
    @Nonnull
    public static BaseConfigScreen create() {
        return new BaseConfigScreen(Reference.MOD_INFO, ConfigHandler.allTabList, ConfigHandler.defaultTab, "potteckit.title.configs", Reference.VERSION);
    }

    public static List<ConfigTab> getConfigTabs() {
        return ConfigHandler.allTabList;
    }
}
