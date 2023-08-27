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
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enable_potteckit", true, "", "enable_potteckit", "enable_potteckit");
    @Config(types = Type.HOTKEY, domains = Domain.GENERIC, notVanilla = false, cheating = false)
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("open_config_screen", "K,C");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, needServer = true, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingGraph = new HotkeyedBooleanConfig("chunk_loading_graph", false, "", "chunk_loading_graph", "chunk_loading_graph");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, needServer = true, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingDetails = new HotkeyedBooleanConfig("chunk_loading_details", false, "", "chunk_loading_details", "chunk_loading_details");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig chunkLoadingGraphReceiver = new HotkeyedBooleanConfig("chunk_loading_graph_receiver", false, "", "chunk_loading_graph_receiver", "chunk_loading_graph_receiver");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig moreProfilerLevels = new HotkeyedBooleanConfig("moreProfilerLevels", false, "");
    //@Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.FIX, enabled = false)
    //public static final HotkeyedBooleanConfig optimizeChunkRenderer = new HotkeyedBooleanConfig("optimizeChunkRenderer", false, "");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.METER)
    public static final HotkeyedBooleanConfig profileImmediateChunkRebuild = new HotkeyedBooleanConfig("profileImmediateChunkRebuild", false, "");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.YEET)
    public static final HotkeyedBooleanConfig yeetChunkRebuild = new HotkeyedBooleanConfig("yeetChunkRebuild", false, "");
    //@Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.YEET, enabled = false)
    //public static final HotkeyedBooleanConfig cyclicReduceChunkRebuild = new HotkeyedBooleanConfig("cyclicReduceChunkRebuild", false, "");
    //@Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.YEET, enabled = false)
    //public static final HotkeyedBooleanConfig randomReduceChunkRebuild = new HotkeyedBooleanConfig("randomReduceChunkRebuild", false, "");
    //@Config(types = Type.NUMBER, domains = Domain.YEET, enabled = false)
    //public static final IntegerConfig chunkRebuildReducedPeriod = new IntegerConfig("chunkRebuildReducedPeriod", 20, 1, 900);
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig autoDisturbChunkRebuild = new HotkeyedBooleanConfig("autoDisturbChunkRebuild", false, "");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig chunkRebuildBuffer = new HotkeyedBooleanConfig("chunkRebuildBuffer", false, "");
    @Config(types = Type.NUMBER, domains = Domain.TWEAK)
    public static final InvIntegerConfig chunkRebuildBufferThreshold = new InvIntegerConfig("chunkRebuildDisturbThreshold", 0.75e+9, 40, 1, 120);
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig asyncNearbyChunkRender = new HotkeyedBooleanConfig("asyncNearbyChunkRender", false, "");
    //@Config(types = {Type.NUMBER, Type.TOGGLE}, domains = Domain.TWEAK, enabled = false)
    //public static final BooleanAndDoubleConfig immediateChunkRenderDaemon = new BooleanAndDoubleConfig("immediateChunkRenderDaemon", false, 20.0, 0.0, 50.0);

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
