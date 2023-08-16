package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {
    @Inject(method = "init", at = @At(value = "HEAD"))
    public void onInitStart(CallbackInfoReturnable<Boolean> cir) {
        MutablePhaseClock.INSTANCE.startPhase(GamePhase.SP_INITIAL_LOAD);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void onInitFinish(CallbackInfoReturnable<Boolean> cir) {
        MutablePhaseClock.INSTANCE.endPhase(GamePhase.SP_INITIAL_LOAD);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;getPlayerList()Lnet/minecraft/server/management/PlayerList;", ordinal = 0))
    public void onPauseSaveStart(CallbackInfo ci) {
        MutablePhaseClock.INSTANCE.startPhase(GamePhase.SP_SAVE_ON_PAUSE);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;saveAllWorlds(Z)V"))
    public void onPauseSaveFinish(@Nonnull IntegratedServer self, boolean isSilent) {
        self.saveAllWorlds(isSilent);
        MutablePhaseClock.INSTANCE.endPhase(GamePhase.SP_SAVE_ON_PAUSE);
    }
}
