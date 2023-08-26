package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    @Unique
    protected final MutablePhaseClock potatoTechKit$clock = MutablePhaseClock.instanceFromServer(getServer());

    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/System;nanoTime()J", ordinal = 0))
    public void beforeTickIncrement(CallbackInfo ci) {
        potatoTechKit$clock.updateStatus();
        potatoTechKit$clock.syncFromConfigs();
        potatoTechKit$clock.pushPhase(GamePhase.SERVER_TICK_COUNT);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;startProfiling:Z", opcode = Opcodes.GETFIELD))
    public void afterTickIncrement(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public void beforeQueuedTask(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.INGAME_QUEUED_TASK);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 1))
    public void afterQueuedTask(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 2))
    public void startDimension(CallbackInfo ci) {
        potatoTechKit$clock.startNextDimension();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
    public void beforeTimeSync(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.CLIENT_TIME_SYNC);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 4))
    public void afterTimeSync(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 10))
    public void endDimension(CallbackInfo ci) {
        potatoTechKit$clock.setDimension(null);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getNetworkSystem()Lnet/minecraft/network/NetworkSystem;"))
    public void beforeNetworkTick(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.CONNECTION_UPDATE);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 11))
    public void afterNetworkTick(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;playerList:Lnet/minecraft/server/management/PlayerList;", opcode = Opcodes.GETFIELD, ordinal = 1))
    public void beforePlayerListTick(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.PLAYER_LIST_TICK);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 12))
    public void afterPlayerListTick(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getFunctionManager()Lnet/minecraft/advancements/FunctionManager;"))
    public void beforeCommandFunction(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.COMMAND_FUNCTION);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 13))
    public void afterCommandFunction(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;playerList:Lnet/minecraft/server/management/PlayerList;", opcode = Opcodes.GETFIELD, ordinal = 1))
    public void beforeAutoSave(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SERVER_AUTO_SAVE);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 4))
    public void afterAutoSave(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }
}
