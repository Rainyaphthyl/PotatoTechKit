package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public enum GamePhase {
    /////////////////////////////////////
    // The Game Phases are comparable! //
    // Do NOT change the order! /////////
    /////////////////////////////////////
    SERVER_TICK_COUNT(false, "Tick Counting"),
    INGAME_QUEUED_TASK(false, "Queued Tasks in Game", "QT"),
    WEATHER_UPDATE(true, "Weather Update", "WU"),
    SLEEP_AND_DAYLIGHT(true, "Sleep and Daylight", "SDL"),
    MOB_SPAWNING(true, "Mob Spawning", "MS"),
    CHUNK_UNLOAD(true, "Chunk Unload", "CU"),
    WORLD_TIME_UPDATE(true, "World Time Counting", "WTC"),
    TILE_TICK(true, "Tile Tick", "TT"),
    CHUNK_TICK(true, "Chunk Tick", "CRT"),
    PLAYER_CHUNK_MAP(true, "Player Chunk Map", "PCM"),
    VILLAGE_TICK(true, "Village Tick", "VT"),
    VILLAGE_SIEGE(true, "Village Siege", "VS"),
    PORTAL_REMOVAL(true, "Portal Removal", "PR"),
    BLOCK_EVENT(true, "Block Events", "BE"),
    WORLD_IDLE_CHECK(true, "World Idle Check", "WIC"),
    DRAGON_FIGHT(true, "Dragon Fight", "DF"),
    GLOBAL_ENTITY_UPDATE(true, "Global Entity Update"),
    ENTITY_UNLOAD(true, "Entity Unload", "ER"),
    PLAYER_UPDATE(true, "Player Entities", "PE"),
    ENTITY_UPDATE(true, "Entity Update", "EU"),
    TILE_ENTITY_UNLOAD(true, "Tile Entity Unload", "TER"),
    TILE_ENTITY_UPDATE(true, "Tile Entity Update", "TEU"),
    TILE_ENTITY_PENDING(true, "Tile Entity Appending", "TEA"),
    ENTITY_TRACKING(true, "Entity Tracking", "ET"),
    CONNECTION_UPDATE(false, "Connection Update", "NU"),
    PACKET_SENDING(false, "Player Packet Sending", "PPS"),
    COMMAND_FUNCTION(false, "Command Function", "CF"),
    SERVER_AUTO_SAVE(false, "Periodic Auto-save", "AS"),
    SP_VIEW_DISTANCE_ALT(false, "View Distance Alteration"),
    SP_DIFFICULTY_LOCK(false, "Difficulty Lock Check"),
    /////////////////////////////////////////
    // Things below should be at the first //
    /////////////////////////////////////////
    SP_INITIAL_LOAD(false, "Initialization", "Init"),
    SP_SAVE_ON_PAUSE(false, "Auto-save on Pause", "ASP"),
    SP_TASK_ON_PAUSE(false, "Queued Tasks on Pause", "QTP"),
    /////////////////////////////////////////
    // Things above should be at the first //
    /////////////////////////////////////////
    SERVER_STOP(false, "Server Shutdown", "Exit");
    public final boolean dimensional;
    public final String description;
    public final String shortName;
    private final ITextComponent component;

    GamePhase(boolean dimensional, String description) {
        this(dimensional, description, description);
    }

    GamePhase(boolean dimensional, String description, String shortName) {
        this.dimensional = dimensional;
        this.description = description;
        this.shortName = shortName;
        component = new TextComponentString(this.shortName);
        if (!Objects.equals(description, shortName)) {
            Style style = new Style();
            ITextComponent hover = new TextComponentString("(" + ordinal() + ')' + description);
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
            component.setStyle(style);
        }
    }

    public static boolean isNullOrDimensional(GamePhase phase) {
        return phase == null || phase.dimensional;
    }

    public static String getDescription(@Nullable GamePhase phase) {
        return phase == null ? String.valueOf((Object) null) : phase.description;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @Nonnull
    public ITextComponent getHoveredName() {
        return component.createCopy();
    }
}
