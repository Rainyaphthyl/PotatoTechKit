package io.github.rainyaphthyl.potteckit.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.annotation.Config;
import io.github.rainyaphthyl.potteckit.config.annotation.Domain;
import io.github.rainyaphthyl.potteckit.config.annotation.Type;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
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
    @Config(types = {Type.TOGGLE, Type.HOTKEY}, domains = Domain.YEET, notVanilla = false, cheating = false)
    public static final HotkeyedBooleanConfig reduceImmediateChunkRender = new HotkeyedBooleanConfig("reduceImmediateChunkRender", false, "");
    @Config(types = Type.NUMBER, domains = Domain.YEET, notVanilla = false, cheating = false)
    public static final DoubleConfig immediateChunkRenderRange = new DoubleConfig("immediateChunkRenderRange", 14.0, 0.0, 400.0);
    @Config(domains = Domain.YEET, notVanilla = false, cheating = false)
    public static final OptionListConfig<ChunkRenderYeetMode> chunkRenderYeetMode = new OptionListConfig<>("chunkRenderYeetMode", ChunkRenderYeetMode.INVOKE, ChunkRenderYeetMode.list);

    public static void registerOnInit() {
        JsonModConfig jsonModConfig = new JsonModConfig(Reference.MOD_INFO, Reference.CONFIG_VERSION, ConfigHandler.optionCategoryList);
        Registry.CONFIG_MANAGER.registerConfigHandler(jsonModConfig);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, GuiConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, GuiConfigScreen::getConfigTabs);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(new PotteckitHotkeyProvider());
        Actions.init();
        Callbacks.init();
    }

    public enum ChunkRenderYeetMode implements OptionListConfigValue {
        INVOKE("invoke"),
        FIELD("field");
        public static final ImmutableList<ChunkRenderYeetMode> list = ImmutableList.of(INVOKE, FIELD);
        private final String name;

        ChunkRenderYeetMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
