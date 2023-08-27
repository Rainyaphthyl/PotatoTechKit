package io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph;

import net.minecraft.util.text.TextFormatting;

public enum ChunkEvent {
    GENERATING("Generating", "is generated", TextFormatting.LIGHT_PURPLE, false),
    LOADING("Loading", "is loaded", TextFormatting.AQUA, false),
    CANCEL_UNLOAD("Cancel Unload", "cancels unloading", TextFormatting.DARK_GREEN, true),
    QUEUE_UNLOAD("Queue Unload", "queues unloading", TextFormatting.GOLD, true),
    UNLOADING("Unloading", "is unloaded", TextFormatting.RED, false);
    public final String name;
    public final String description;
    public final TextFormatting color;
    public final boolean silent;

    ChunkEvent(String name, String description, TextFormatting color, boolean silent) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.silent = silent;
    }
}
