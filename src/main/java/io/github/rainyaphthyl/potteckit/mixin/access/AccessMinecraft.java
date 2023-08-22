package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface AccessMinecraft {
    @Accessor(value = "startNanoTime")
    long getStartNanoTime();
}
