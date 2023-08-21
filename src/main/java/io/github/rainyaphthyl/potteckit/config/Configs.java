package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.config.annotation.Type;
import io.github.rainyaphthyl.potteckit.config.option.InvIntegerConfig;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.gui.InvIntegerConfigWidget;
import io.github.rainyaphthyl.potteckit.input.PotteckitHotkeyProvider;
import io.github.rainyaphthyl.potteckit.util.Reference;

public class Configs {
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enablePotteckit", true, "");
    @Config(types = Type.HOTKEY, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("openConfigScreen", "K,C");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig moreProfilerLevels = new HotkeyedBooleanConfig("moreProfilerLevels", false, "");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.FIX, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig optimizeChunkRenderer = new HotkeyedBooleanConfig("optimizeChunkRenderer", false, "");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig profileImmediateChunkRebuild = new HotkeyedBooleanConfig("profileImmediateChunkRebuild", false, "");
    @Config(domains = Domain.YEET, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig yeetChunkRebuild = new HotkeyedBooleanConfig("yeetChunkRebuild", false, "");
    @Config(domains = Domain.YEET, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig autoDisturbChunkRebuild = new HotkeyedBooleanConfig("autoDisturbChunkRebuild", false, "");
    @Config(domains = Domain.TWEAK, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkRebuildBuffer = new HotkeyedBooleanConfig("chunkRebuildBuffer", false, "");
    @Config(domains = Domain.TWEAK, notVanilla = false, cheating = false)
    public static final InvIntegerConfig chunkRebuildBufferThreshold = new InvIntegerConfig("chunkRebuildDisturbThreshold", 0.75e+9, 40, 1, 120);

    public static void registerOnInit() {
        JsonModConfig jsonModConfig = new JsonModConfig(Reference.MOD_INFO, Reference.CONFIG_VERSION, ConfigHandler.optionCategoryList);
        Registry.CONFIG_MANAGER.registerConfigHandler(jsonModConfig);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, GuiConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, GuiConfigScreen::getConfigTabs);
        Registry.CONFIG_WIDGET.registerConfigWidgetFactory(InvIntegerConfig.class, InvIntegerConfigWidget::new);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new PotteckitHotkeyProvider());
        Actions.init();
        Callbacks.init();
    }
}
