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
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = {Domain.GENERIC, Domain.TWEAK})
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enable_potteckit", true, "", "enable_potteckit", "enable_potteckit");
    @Config(types = Type.HOTKEY, domains = {Domain.GENERIC, Domain.ACTION})
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("open_config_screen", "K,C");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig chunkLoadingGraph = new HotkeyedBooleanConfig("chunk_loading_graph", false, "", "chunk_loading_graph", "chunk_loading_graph");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig chunkLoadingDetails = new HotkeyedBooleanConfig("chunk_loading_details", false, "", "chunk_loading_details", "chunk_loading_details");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER)
    public static final HotkeyedBooleanConfig chunkLoadingGraphReceiver = new HotkeyedBooleanConfig("chunk_loading_graph_receiver", false, "", "chunk_loading_graph_receiver", "chunk_loading_graph_receiver");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig moreProfilerLevels = new HotkeyedBooleanConfig("more_profiler_levels", false, "", "more_profiler_levels", "more_profiler_levels");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER)
    public static final HotkeyedBooleanConfig profileImmediateChunkRebuild = new HotkeyedBooleanConfig("profile_immediate_chunk_rebuild", false, "", "profile_immediate_chunk_rebuild", "profile_immediate_chunk_rebuild");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.YEET)
    public static final HotkeyedBooleanConfig yeetChunkRebuild = new HotkeyedBooleanConfig("yeet_chunk_rebuild", false, "", "yeet_chunk_rebuild", "yeet_chunk_rebuild");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig autoDisturbChunkRebuild = new HotkeyedBooleanConfig("auto_disturb_chunk_rebuild", false, "", "auto_disturb_chunk_rebuild", "auto_disturb_chunk_rebuild");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig chunkRebuildBuffer = new HotkeyedBooleanConfig("chunk_rebuild_buffer", false, "", "chunk_rebuild_buffer", "chunk_rebuild_buffer");
    @Config(types = Type.NUMBER, domains = Domain.TWEAK)
    public static final InvIntegerConfig chunkRebuildBufferThreshold = new InvIntegerConfig("chunk_rebuild_disturb_threshold", 0.75e+9, 40, 1, 120, "chunk_rebuild_disturb_threshold");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig asyncNearbyChunkRender = new HotkeyedBooleanConfig("async_nearby_chunk_render", false, "", "async_nearby_chunk_render", "async_nearby_chunk_render");

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
