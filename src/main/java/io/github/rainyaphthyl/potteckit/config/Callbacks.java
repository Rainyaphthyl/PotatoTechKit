package io.github.rainyaphthyl.potteckit.config;

public class Callbacks {
    public static void init() {
        Configs.ACTIONS.OPEN_CONFIG_PANEL.createCallbackForAction(Actions.OPEN_CONFIG_SCREEN);
    }
}
