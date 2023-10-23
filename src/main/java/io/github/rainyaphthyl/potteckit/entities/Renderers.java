package io.github.rainyaphthyl.potteckit.entities;

import fi.dy.masa.malilib.render.overlay.OverlayRendererContainer;

public class Renderers {
    public static final ProjectileAimRenderer PROJECTILE_AIM_RENDERER = new ProjectileAimRenderer();

    public static void init() {
        OverlayRendererContainer.INSTANCE.addRenderer(PROJECTILE_AIM_RENDERER);
    }
}
