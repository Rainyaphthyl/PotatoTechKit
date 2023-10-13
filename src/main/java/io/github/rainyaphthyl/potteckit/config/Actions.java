package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.util.Reference;

public class Actions {
    public static final NamedAction OPEN_CONFIG_SCREEN = ActionUtils.register(Reference.MOD_INFO, "openConfigScreen", () -> BaseScreen.openScreen(GuiConfigScreen.create()));
    public static final NamedAction INDICATE_PROJECTILE = ActionUtils.register(Reference.MOD_INFO, "indicateProjectile", () -> {
    });

    public static void init() {
    }
}
