package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock.SubPhaseClock;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public enum GamePhase {
    /////////////////////////////////////
    // The Game Phases are comparable! //
    // Do NOT change the order! /////////
    /////////////////////////////////////
    SERVER_TICK_COUNT(false, "Tick Counting", "TC"),
    INGAME_QUEUED_TASK(false, "Queued Tasks in Game", "QT"),
    CLIENT_TIME_SYNC(true, "Client Time Sync", "CTS"),
    WEATHER_UPDATE(true, "Weather Update", "WU"),
    HARDCODE_DIFFICULTY(true, "Hardcode Difficulty Lock", "HDL"),
    SLEEP_AND_DAYTIME(true, "Sleep and Daytime", "SDT"),
    MOB_SPAWNING(true, "Mob Spawning", "MS"),
    CHUNK_UNLOAD(true, "Chunk Unload", "CU"),
    WORLD_TIME_UPDATE(true, "World Time Counting", "WTC"),
    TILE_TICK(true, "Tile Tick", "TT", TileTickSubPhase.class, TileTickClock.class),
    PLAYER_LIGHT_CHECK(true, "Player Light Check", "PLC"),
    CHUNK_TICK(true, "Chunk Tick", "CT"),
    PLAYER_CHUNK_MAP(true, "Player Chunk Map", "PCM"),
    VILLAGE_TICK(true, "Village Tick", "VT"),
    VILLAGE_SIEGE(true, "Village Siege", "VS"),
    PORTAL_REMOVAL(true, "Portal Removal", "PR"),
    BLOCK_EVENT(true, "Block Events", "BE", BlockEventSubPhase.class, BlockEventClock.class),
    WORLD_IDLE_CHECK(true, "World Idle Check", "WIC"),
    DRAGON_FIGHT(true, "Dragon Fight", "DF"),
    GLOBAL_ENTITY_UPDATE(true, "Global Entity Update", "GE"),
    ENTITY_UNLOAD(true, "Entity Unload", "ER"),
    PLAYER_UPDATE(true, "Player Entities", "PE"),
    ENTITY_UPDATE(true, "Entity Update", "EU"),
    TILE_ENTITY_UNLOAD(true, "Tile Entity Unload", "TER"),
    TILE_ENTITY_UPDATE(true, "Tile Entity Update", "TEU"),
    TILE_ENTITY_PENDING(true, "Tile Entity Appending", "TEA"),
    ENTITY_TRACKING(true, "Entity Tracking", "ET"),
    CONNECTION_UPDATE(false, "Connection Update", "NU"),
    PLAYER_LIST_TICK(false, "Player List Tick", "PLT"),
    COMMAND_FUNCTION(false, "Command Function", "CF"),
    SERVER_AUTO_SAVE(false, "Periodic Auto-save", "AS"),
    SP_VIEW_DISTANCE_ALT(false, "View Distance Alteration", "VDA"),
    SP_DIFFICULTY_ALT(false, "Difficulty Alteration", "DA"),
    /////////////////////////////////////////
    // Things below should be at the first //
    /////////////////////////////////////////
    SP_INITIAL_LOAD(false, true, "Initial Loading", "Init"),
    SP_SAVE_ON_PAUSE(false, true, "Auto-save on Pause", "ASP"),
    SP_TASK_ON_PAUSE(false, true, "Queued Tasks on Pause", "QTP"),
    /////////////////////////////////////////
    // Things above should be at the first //
    /////////////////////////////////////////
    SERVER_STOP(false, true, "Server Shutdown", "Exit");
    private static final Map<String, GamePhase> phaseMapByName = new HashMap<>();

    static {
        for (GamePhase phase : GamePhase.values()) {
            GamePhase previous = phaseMapByName.putIfAbsent(phase.shortName, phase);
            if (previous != null) {
                throw new IllegalArgumentException();
            }
        }
    }

    public final boolean dimensional;
    /**
     * Tasks during game pause, before server starting, or on server stopping.
     */
    public final boolean outOfTick;
    public final String description;
    public final String shortName;
    public final Class<? extends SubPhase> subClass;
    public final Class<? extends SubPhaseClock> clockClass;
    private final ITextComponent component;

    GamePhase(boolean dimensional, String description, String shortName) {
        this(dimensional, description, shortName, null, null);
    }

    GamePhase(boolean dimensional, boolean outOfTick, String description, String shortName) {
        this(dimensional, outOfTick, description, shortName, null, null);
    }

    GamePhase(boolean dimensional, String description, String shortName, Class<? extends SubPhase> subClass, Class<? extends SubPhaseClock> clockClass) {
        this(dimensional, false, description, shortName, subClass, clockClass);
    }

    GamePhase(boolean dimensional, boolean outOfTick, String description, String shortName, Class<? extends SubPhase> subClass, Class<? extends SubPhaseClock> clockClass) {
        this.dimensional = dimensional;
        this.outOfTick = outOfTick;
        this.description = description;
        this.shortName = shortName;
        this.subClass = subClass;
        this.clockClass = clockClass;
        if ((subClass == null) != (clockClass == null)) throw new NullPointerException();
        component = new TextComponentString(this.shortName);
        Style style = new Style();
        ITextComponent hover = new TextComponentString("(" + ordinal() + ')' + description);
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        component.setStyle(style);
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

    @Nullable
    public SubPhaseClock createSubPhaseClock(MutablePhaseClock tickClock) {
        if (clockClass == null) {
            return null;
        } else {
            try {
                Constructor<? extends SubPhaseClock> constructor = clockClass.getConstructor(MutablePhaseClock.class);
                return constructor.newInstance(tickClock);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }

    @Nonnull
    public ITextComponent getHoveredName() {
        return component.createCopy();
    }
}
