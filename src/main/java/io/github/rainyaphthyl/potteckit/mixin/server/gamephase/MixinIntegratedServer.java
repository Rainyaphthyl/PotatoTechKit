package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MixinMinecraftServer {
    @Inject(method = "init", at = @At(value = "HEAD"))
    public void beforeInit(CallbackInfoReturnable<Boolean> cir) {
        potatoTechKit$clock.pushPhase(GamePhase.SP_INITIAL_LOAD);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    public void afterInit(CallbackInfoReturnable<Boolean> cir) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;getPlayerList()Lnet/minecraft/server/management/PlayerList;", ordinal = 0))
    public void beforePauseSave(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SP_SAVE_ON_PAUSE);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;saveAllWorlds(Z)V"))
    public void afterPauseSave(@Nonnull IntegratedServer self, boolean isSilent) {
        self.saveAllWorlds(isSilent);
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/integrated/IntegratedServer;futureTaskQueue:Ljava/util/Queue;", opcode = Opcodes.GETFIELD, ordinal = 0, shift = At.Shift.AFTER))
    public void beforePauseTask(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SP_TASK_ON_PAUSE);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/integrated/IntegratedServer;mc:Lnet/minecraft/client/Minecraft;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public void beforeAltViewDistance(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SP_VIEW_DISTANCE_ALT);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/integrated/IntegratedServer;mc:Lnet/minecraft/client/Minecraft;", opcode = Opcodes.GETFIELD, ordinal = 3))
    public void beforeLockDifficulty(CallbackInfo ci) {
        potatoTechKit$clock.swapPhase(GamePhase.SP_DIFFICULTY_ALT);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void afterIntegratedTick(CallbackInfo ci) {
        potatoTechKit$clock.popPhaseIfPresent(GamePhase.SP_TASK_ON_PAUSE);
        potatoTechKit$clock.popPhaseIfPresent(GamePhase.SP_DIFFICULTY_ALT);
    }

    @Inject(method = "stopServer", at = @At(value = "HEAD"))
    public void beforeServerStop(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SERVER_STOP);
    }

    @Inject(method = "stopServer", at = @At(value = "RETURN"))
    public void afterServerStop(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }
}
