package io.github.rainyaphthyl.potteckit.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.list.BlockListConfig;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.config.annotation.Type;
import io.github.rainyaphthyl.potteckit.config.option.EnumRealmStatus;
import io.github.rainyaphthyl.potteckit.config.option.InvIntegerConfig;
import io.github.rainyaphthyl.potteckit.config.option.multipart.ChunkFilterListConfig;
import io.github.rainyaphthyl.potteckit.gui.ChunkFilterListConfigWidget;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.gui.InvIntegerConfigWidget;
import io.github.rainyaphthyl.potteckit.input.PotteckitHotkeyProvider;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.init.Blocks;

public class Configs {
    @Config(types = Type.TOGGLE, domains = {Domain.GENERIC, Domain.TWEAK})
    public static final HotkeyedBooleanConfig enablePotteckit = new HotkeyedBooleanConfig("enable_potteckit", true, "", "enable_potteckit", "enable_potteckit");
    @Config(types = Type.HOTKEY, domains = {Domain.GENERIC, Domain.ACTION})
    public static final HotkeyConfig openConfigScreen = new HotkeyConfig("open_config_screen", "K,C");
    @Config(types = Type.TOGGLE, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig chunkLoadingGraph = new HotkeyedBooleanConfig("chunk_loading_graph", false, "", "chunk_loading_graph", "chunk_loading_graph");
    @Config(types = Type.TOGGLE, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig chunkLoadingDetails = new HotkeyedBooleanConfig("chunk_loading_details", false, "", "chunk_loading_details", "chunk_loading_details");
    @Config(types = Type.TOGGLE, domains = Domain.METER)
    public static final HotkeyedBooleanConfig chunkLoadingGraphReceiver = new HotkeyedBooleanConfig("chunk_loading_graph_receiver", false, "", "chunk_loading_graph_receiver", "chunk_loading_graph_receiver");
    @Config(types = Type.TOGGLE, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig moreProfilerLevels = new HotkeyedBooleanConfig("more_profiler_levels", false, "", "more_profiler_levels", "more_profiler_levels");
    @Config(types = Type.TOGGLE, domains = Domain.METER)
    public static final HotkeyedBooleanConfig profileImmediateChunkRebuild = new HotkeyedBooleanConfig("profile_immediate_chunk_rebuild", false, "", "profile_immediate_chunk_rebuild", "profile_immediate_chunk_rebuild");
    @Config(types = Type.TOGGLE, domains = Domain.YEET)
    public static final HotkeyedBooleanConfig yeetChunkRebuild = new HotkeyedBooleanConfig("yeet_chunk_rebuild", false, "", "yeet_chunk_rebuild", "yeet_chunk_rebuild");
    @Config(types = Type.TOGGLE, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig autoDisturbChunkRebuild = new HotkeyedBooleanConfig("auto_disturb_chunk_rebuild", false, "", "auto_disturb_chunk_rebuild", "auto_disturb_chunk_rebuild");
    @Config(types = Type.TOGGLE, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig chunkRebuildBuffer = new HotkeyedBooleanConfig("chunk_rebuild_buffer", false, "", "chunk_rebuild_buffer", "chunk_rebuild_buffer");
    @Config(types = Type.NUMBER, domains = Domain.TWEAK)
    public static final InvIntegerConfig chunkRebuildBufferThreshold = new InvIntegerConfig("chunk_rebuild_disturb_threshold", 0.75e+9, 40, 1, 120, "chunk_rebuild_disturb_threshold");
    @Config(types = Type.TOGGLE, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig chunkRebuildAutoBlacklist = new HotkeyedBooleanConfig("chunk_rebuild_auto_blacklist", false, "", "chunk_rebuild_auto_blacklist", "chunk_rebuild_auto_blacklist");
    @Config(types = Type.NUMBER, domains = Domain.TWEAK)
    public static final IntegerConfig chunkRebuildBlacklistThreshold = new IntegerConfig("chunk_rebuild_blacklist_threshold", 200, 0, 1000, "chunk_rebuild_blacklist_threshold");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK)
    public static final HotkeyedBooleanConfig asyncNearbyChunkRender = new HotkeyedBooleanConfig("async_nearby_chunk_render", false, "", "async_nearby_chunk_render", "async_nearby_chunk_render");
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.TWEAK, serverSide = true)
    public static final HotkeyedBooleanConfig blockStateTextureSync = new HotkeyedBooleanConfig("block_state_texture_sync", false, "", "block_state_texture_sync", "block_state_texture_sync");
    @Config(types = Type.LIST, domains = Domain.TWEAK, serverSide = true)
    public static final BlockListConfig blockStateTextureSyncList = BlockListConfig.create("block_state_texture_sync_list", ImmutableList.of(Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER));
    @Config(types = Type.TOGGLE, domains = Domain.FIX, serverSide = true)
    public static final HotkeyedBooleanConfig optifineSpawningFix = new HotkeyedBooleanConfig("optifine_spawning_fix", false, "", "optifine_spawning_fix", "optifine_spawning_fix");
    @Config(types = Type.TOGGLE, domains = Domain.FIX, serverSide = true)
    public static final HotkeyedBooleanConfig fixLanQuittingFreeze = new HotkeyedBooleanConfig("fix_lan_quit_freeze", true, "", "fix_lan_quit_freeze", "fix_lan_quit_freeze");
    @Config(types = Type.TOGGLE, domains = Domain.FIX, serverSide = true)
    public static final HotkeyedBooleanConfig fixLanSkinAbsence = new HotkeyedBooleanConfig("fix_lan_skin_absence", true, "", "fix_lan_skin_absence", "fix_lan_skin_absence");
    @Config(types = Type.ENUM, domains = Domain.YEET)
    public static final OptionListConfig<EnumRealmStatus> yeetRealmPage = new OptionListConfig<>("yeet_realm_page", EnumRealmStatus.DISABLED, EnumRealmStatus.list, "yeet_realm_page", "yeet_realm_page");
    @Config(types = Type.LIST, domains = Domain.METER)
    public static final ChunkFilterListConfig chunkLoadFilterList = ChunkFilterListConfig.create("chunk_load_filter", ImmutableList.of());
    @Config(types = Type.TOGGLE, domains = Domain.METER)
    public static final HotkeyedBooleanConfig chunkLoadFilterSwitch = new HotkeyedBooleanConfig("chunk_load_filter_switch", false, "", "chunk_load_filter_switch", "chunk_load_filter_switch");
    @Config(types = Type.TOGGLE, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig logSaveState = new HotkeyedBooleanConfig("log_save_state", false, "", "log_save_state", "log_save_state");
    @Config(types = Type.TOGGLE, domains = Domain.METER, serverSide = true)
    public static final HotkeyedBooleanConfig logInvalidEndCity = new HotkeyedBooleanConfig("log_invalid_end_city", false, "", "log_invalid_end_city", "log_invalid_end_city");

    public static void registerOnInit() {
        JsonModConfig jsonModConfig = new JsonModConfig(Reference.MOD_INFO, Reference.CONFIG_VERSION, ConfigHandler.optionCategoryList);
        Registry.CONFIG_MANAGER.registerConfigHandler(jsonModConfig);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, GuiConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, GuiConfigScreen::getConfigTabs);
        Registry.CONFIG_WIDGET.registerConfigWidgetFactory(InvIntegerConfig.class, InvIntegerConfigWidget::new);
        Registry.CONFIG_WIDGET.registerConfigWidgetFactory(ChunkFilterListConfig.class, ChunkFilterListConfigWidget::new);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new PotteckitHotkeyProvider());
        Actions.init();
        Callbacks.init();
    }
}
