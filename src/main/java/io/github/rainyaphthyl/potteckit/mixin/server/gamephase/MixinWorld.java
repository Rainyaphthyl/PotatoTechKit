package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow
    @Final
    public WorldProvider provider;
    @Shadow
    @Final
    public boolean isRemote;
    @Unique
    protected MutablePhaseClock potatoTechKit$clock = null;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onConstruct(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client, CallbackInfo ci) {
        if (isRemote) {
            potatoTechKit$clock = null;
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "HEAD"))
    public void beforeWeather(CallbackInfo ci) {
        if (!isRemote) {
            potatoTechKit$clock.pushPhase(GamePhase.WEATHER_UPDATE);
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "RETURN"))
    public void afterWeather(CallbackInfo ci) {
        if (!isRemote) {
            potatoTechKit$clock.popPhase();
        }
    }
}
