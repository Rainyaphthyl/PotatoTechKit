package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends MixinWorld {
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onConstruct(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn, CallbackInfo ci) {
        potatoTechKit$clock = MutablePhaseClock.instanceFromServer(server);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void startDimension(CallbackInfo ci) {
        DimensionType dimension = provider.getDimensionType();
        potatoTechKit$clock.setDimension(dimension);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;areAllPlayersAsleep()Z"))
    public void beforeSleep(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SLEEP_AND_WAKE);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public void afterSleep(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getGameRules()Lnet/minecraft/world/GameRules;", ordinal = 1))
    public void beforeMobSpawning(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.MOB_SPAWNING);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 1))
    public void afterMobSpawning(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;chunkProvider:Lnet/minecraft/world/chunk/IChunkProvider;", opcode = Opcodes.GETFIELD))
    public void beforeChunkUnload(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.CHUNK_UNLOAD);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/chunk/IChunkProvider;tick()Z"))
    public void afterChunkUnload(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;worldInfo:Lnet/minecraft/world/storage/WorldInfo;", opcode = Opcodes.GETFIELD, ordinal = 4))
    public void beforeWorldTimeUpdate(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.WORLD_TIME_UPDATE);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 2))
    public void afterWorldTimeUpdate(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tickUpdates", at = @At(value = "HEAD"))
    public void beforeTileTick(boolean runAllPending, CallbackInfoReturnable<Boolean> cir) {
        potatoTechKit$clock.pushPhase(GamePhase.TILE_TICK);
    }

    @Inject(method = "tickUpdates", at = @At(value = "RETURN"))
    public void afterTileTick(boolean runAllPending, CallbackInfoReturnable<Boolean> cir) {
        potatoTechKit$clock.popPhase();
    }
}
