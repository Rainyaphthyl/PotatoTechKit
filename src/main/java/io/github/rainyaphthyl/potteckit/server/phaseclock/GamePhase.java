package io.github.rainyaphthyl.potteckit.server.phaseclock;

import javax.annotation.Nullable;

public enum GamePhase {
    SERVER_TICK_COUNT(false, "Tick Counting"),
    INGAME_QUEUED_TASK(false, "Queued Tasks in Game", "QU"),
    WEATHER_UPDATE(true, "Weather Update", "Weather"),
    SLEEP_AND_WAKE(true, "Sleep and Wake", "Sleep"),
    MOB_SPAWNING(true, "Mob Spawning", "Spawn"),
    CHUNK_UNLOAD(true, "Chunk Unload", "CU"),
    WORLD_TIME_UPDATE(true, "World Time Counting"),
    TILE_TICK(true, "Tile Tick", "TT"),
    CHUNK_TICK(true, "Chunk Tick", "CRT"),
    PLAYER_CHUNK_MAP(true, "Player Chunk Map"),
    VILLAGE_TICK(true, "Village Tick", "VT"),
    VILLAGE_SIEGE(true, "Village Siege", "VS"),
    BLOCK_EVENT(true, "Block Events", "BE"),
    WORLD_IDLE_CHECK(true, "World Idle Check", "WU"),
    DRAGON_FIGHT(true, "Dragon Fight"),
    GLOBAL_ENTITY_UPDATE(true, "Global Entity Update"),
    ENTITY_UNLOAD(true, "Entity Unload", "ER"),
    PLAYER_UPDATE(true, "Player Entities"),
    ENTITY_UPDATE(true, "Entity Update", "EU"),
    TILE_ENTITY_UNLOAD(true, "Tile Entity Unload", "TER"),
    TILE_ENTITY_UPDATE(true, "Tile Entity Update", "TEU"),
    TILE_ENTITY_PENDING(true, "Tile Entity Appending", "TEA"),
    ENTITY_TRACKING(true, "Entity Tracking"),
    CONNECTION_UPDATE(false, "Connection Update", "NU"),
    PACKET_SENDING(false, "Packet Sending"),
    COMMAND_FUNCTION(false, "Command Function", "Command"),
    SERVER_AUTO_SAVE(false, "Periodic Auto-save", "Autosave"),
    SP_VIEW_DISTANCE_ALT(false, "View Distance Alteration"),
    SP_DIFFICULTY_LOCK(false, "Difficulty Lock Check"),
    /////////////////////////////////////////
    // Things below should be at the first //
    /////////////////////////////////////////
    SP_INITIAL_LOAD(false, "Initialization", "Init"),
    SP_SAVE_ON_PAUSE(false, "Auto-save on Pause", "Pause"),
    SP_TASK_ON_PAUSE(false, "Queued Tasks on Pause"),
    /////////////////////////////////////////
    // Things above should be at the first //
    /////////////////////////////////////////
    SERVER_STOP(false, "Server Shutdown", "Exit");
    public final boolean dimensional;
    public final String description;
    public final String shortName;

    GamePhase(boolean dimensional, String description) {
        this(dimensional, description, description);
    }

    GamePhase(boolean dimensional, String description, String shortName) {
        this.dimensional = dimensional;
        this.description = description;
        this.shortName = shortName;
    }

    public static boolean isNullOrDimensional(GamePhase phase) {
        return phase == null || phase.dimensional;
    }

    public static String getShortName(@Nullable GamePhase phase) {
        return phase == null ? String.valueOf((Object) null) : phase.shortName;
    }

    @Override
    public String toString() {
        return description;
    }
}
