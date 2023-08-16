package io.github.rainyaphthyl.potteckit.server;

public enum GamePhase {
    /**
     * {@link net.minecraft.server.integrated.IntegratedServer IntegratedServer}
     */
    SP_SAVE_ON_PAUSE(false),
    /**
     * {@link net.minecraft.server.integrated.IntegratedServer IntegratedServer}
     */
    SP_TASK_ON_PAUSE(false),
    SERVER_TICK_COUNT(false),
    INGAME_QUEUED_TASK(false),
    WEATHER_UPDATE(true),
    MOB_SPAWNING(true),
    CHUNK_UNLOAD(true),
    WORLD_TIME_UPDATE(true),
    TILE_TICK(true),
    CHUNK_TICK(true),
    PLAYER_CHUNK_MAP(true),
    VILLAGE_TICK(true),
    VILLAGE_SIEGE(true),
    BLOCK_EVENT(true),
    WORLD_IDLE_CHECK(true),
    DRAGON_FIGHT(true),
    GLOBAL_ENTITY_UPDATE(true),
    /**
     * Unload is not Removal
     */
    ENTITY_UNLOAD(true),
    PLAYER_UPDATE(true),
    ENTITY_UPDATE(true),
    TILE_ENTITY_UNLOAD(true),
    TILE_ENTITY_UPDATE(true),
    TILE_ENTITY_PENDING(true),
    ENTITY_TRACKING(true),
    CONNECTION_UPDATE(false),
    PACKET_SENDING(false),
    COMMAND_FUNCTION(false),
    SERVER_AUTO_SAVE(false),
    /**
     * {@link net.minecraft.server.integrated.IntegratedServer IntegratedServer}
     */
    SP_VIEW_DISTANCE_ALT(false);
    public final boolean dimensional;

    GamePhase(boolean dimensional) {
        this.dimensional = dimensional;
    }
}
