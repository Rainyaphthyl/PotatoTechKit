package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.config.annotation.Type;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.input.PotteckitHotkeyProvider;
import io.github.rainyaphthyl.potteckit.util.Reference;

public class Configs {
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enable_potteckit", true, "", "enable_potteckit", "enable_potteckit");
    @Config(types = Type.HOTKEY, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("open_config_screen", "K,C");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, needServer = true, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingGraph = new HotkeyedBooleanConfig("chunk_loading_graph", false, "", "chunk_loading_graph", "chunk_loading_graph");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, needServer = true, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingDetails = new HotkeyedBooleanConfig("chunk_loading_details", false, "", "chunk_loading_details", "chunk_loading_details");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingGraphReceiver = new HotkeyedBooleanConfig("chunk_loading_graph_receiver", false, "", "chunk_loading_graph_receiver", "chunk_loading_graph_receiver");

    public static void registerOnInit() {
        JsonModConfig jsonModConfig = new JsonModConfig(Reference.MOD_INFO, Reference.CONFIG_VERSION, ConfigHandler.optionCategoryList);
        Registry.CONFIG_MANAGER.registerConfigHandler(jsonModConfig);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, GuiConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, GuiConfigScreen::getConfigTabs);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new PotteckitHotkeyProvider());
        Actions.init();
        Callbacks.init();
    }
}
