package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

public enum ChunkEvent {
    GENERATING("Generating", "is generated", "G", TextFormatting.LIGHT_PURPLE, false),
    POPULATING("Populating", "is populated", "P", TextFormatting.DARK_PURPLE, true),
    LOADING("Loading", "is loaded", "L", TextFormatting.AQUA, false),
    CANCEL_UNLOAD("Cancel Unload", "cancels unloading", "CU", TextFormatting.DARK_GREEN, true),
    QUEUE_UNLOAD("Queue Unload", "queues unloading", "QU", TextFormatting.GOLD, true),
    UNLOADING("Unloading", "is unloaded", "U", TextFormatting.RED, false),
    SAVING("Saving", "is saved", "S", TextFormatting.DARK_RED, true);
    private static final Map<String, ChunkEvent> eventMapByName = new HashMap<>();

    static {
        for (ChunkEvent event : ChunkEvent.values()) {
            ChunkEvent previous = eventMapByName.putIfAbsent(event.shortName, event);
            if (previous != null) {
                throw new IllegalArgumentException();
            }
        }
    }

    public final String name;
    public final String description;
    public final String shortName;
    public final TextFormatting color;
    public final boolean silent;

    ChunkEvent(String name, String description, String shortName, TextFormatting color, boolean silent) {
        this.name = name;
        this.description = description;
        this.shortName = shortName;
        this.color = color;
        this.silent = silent;
    }

    public static ChunkEvent fromShortName(String key) {
        return eventMapByName.get(key);
    }

    @Override
    public String toString() {
        return shortName;
    }
}
