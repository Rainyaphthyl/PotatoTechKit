package io.github.rainyaphthyl.potteckit.config;

public class Callbacks {
    public static void init() {
        Configs.openConfigScreen.createCallbackForAction(Actions.OPEN_CONFIG_SCREEN);
        Configs.projectileAimZoomIn.createCallbackForAction(Actions.PROJECTILE_ZOOM_IN);
        Configs.projectileAimZoomOut.createCallbackForAction(Actions.PROJECTILE_ZOOM_OUT);
    }
}
