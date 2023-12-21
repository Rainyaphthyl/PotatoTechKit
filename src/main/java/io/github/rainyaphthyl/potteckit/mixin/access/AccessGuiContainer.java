package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiContainer.class)
public interface AccessGuiContainer {
    @Accessor(value = "hoveredSlot")
    Slot getHoveredSlot();
}
