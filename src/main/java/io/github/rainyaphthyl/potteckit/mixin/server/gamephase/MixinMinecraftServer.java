package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    @Unique
    protected final MutablePhaseClock potatoTechKit$clock = new MutablePhaseClock(getServer());

    @Shadow
    public abstract MinecraftServer getServer();
}
