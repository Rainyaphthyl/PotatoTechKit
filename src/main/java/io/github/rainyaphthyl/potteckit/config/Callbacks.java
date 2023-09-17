package io.github.rainyaphthyl.potteckit.config;

public class Callbacks {
    public static void init() {
        Configs.openConfigScreen.createCallbackForAction(Actions.OPEN_CONFIG_SCREEN);
        Configs.checkChunkLoader.createCallbackForAction(Actions.CHECK_CHUNK_LOADER);
    }
}
