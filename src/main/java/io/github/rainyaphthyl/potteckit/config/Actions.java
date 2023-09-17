package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import io.github.rainyaphthyl.potteckit.chunkphase.loader.ChunkLoaderChecker;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.util.Reference;

public class Actions {
    public static final NamedAction OPEN_CONFIG_SCREEN = ActionUtils.register(Reference.MOD_INFO, "open_config_screen", () -> BaseScreen.openScreen(GuiConfigScreen.create()));
    public static final NamedAction CHECK_CHUNK_LOADER = ActionUtils.register(Reference.MOD_INFO, "check_chunk_loader", ChunkLoaderChecker::clientCheckChunkLoader);

    public static void init() {
    }
}
