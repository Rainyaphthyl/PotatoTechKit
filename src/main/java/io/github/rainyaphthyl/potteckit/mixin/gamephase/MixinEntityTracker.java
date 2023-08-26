package io.github.rainyaphthyl.potteckit.mixin.gamephase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import net.minecraft.entity.EntityTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.Objects;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker {
    @Unique
    protected MutablePhaseClock potatoTechKit$clock;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onConstruct(@Nonnull WorldServer theWorldIn, CallbackInfo ci) {
        MinecraftServer server = theWorldIn.getMinecraftServer();
        potatoTechKit$clock = MutablePhaseClock.instanceFromServer(server);
        Objects.requireNonNull(potatoTechKit$clock);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void beforeEntityTracking(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.ENTITY_TRACKING);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void afterEntityTracking(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }
}
