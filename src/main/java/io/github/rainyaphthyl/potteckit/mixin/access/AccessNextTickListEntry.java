package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.world.NextTickListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NextTickListEntry.class)
public interface AccessNextTickListEntry {
    @Accessor(value = "tickEntryID")
    long getTickEntryID();
}
