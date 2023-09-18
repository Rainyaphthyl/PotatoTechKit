package io.github.rainyaphthyl.potteckit.core.portal;

import net.minecraft.server.MinecraftServer;

public abstract class PortalSearcher implements Runnable {
    public static final double INTER_DIM_RATE = 8.0;
    public static final double BORDER_WIDTH = 16.0;
    public static final int BORDER_POS = 29999872;
    protected final MinecraftServer server;

    public PortalSearcher(MinecraftServer server) {
        this.server = server;
    }
}
