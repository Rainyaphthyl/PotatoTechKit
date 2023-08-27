package io.github.rainyaphthyl.potteckit.mixin.gamephase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
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

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getWorldInfo()Lnet/minecraft/world/storage/WorldInfo;", ordinal = 0))
    public void beforeDifficultyLock(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.HARDCODE_DIFFICULTY);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;provider:Lnet/minecraft/world/WorldProvider;"))
    public void afterDifficultyLock(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;areAllPlayersAsleep()Z"))
    public void beforeSleep(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.SLEEP_AND_DAYTIME);
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

    @Inject(method = "updateBlocks", at = @At(value = "HEAD"))
    public void beforePlayerLightCheck(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.PLAYER_LIGHT_CHECK);
    }

    @Inject(method = "updateBlocks", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;worldInfo:Lnet/minecraft/world/storage/WorldInfo;"))
    public void beforeChunkTick(CallbackInfo ci) {
        potatoTechKit$clock.swapPhase(GamePhase.CHUNK_TICK);
    }

    @Inject(method = "updateBlocks", at = @At(value = "RETURN"))
    public void afterChunkTick(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;playerChunkMap:Lnet/minecraft/server/management/PlayerChunkMap;", opcode = Opcodes.GETFIELD))
    public void beforePlayerChunkMap(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.PLAYER_CHUNK_MAP);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 5))
    public void afterPlayerChunkMap(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;villageCollection:Lnet/minecraft/village/VillageCollection;", opcode = Opcodes.GETFIELD))
    public void beforeVillageTick(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.VILLAGE_TICK);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;villageSiege:Lnet/minecraft/village/VillageSiege;", opcode = Opcodes.GETFIELD))
    public void swapVillageTickSiege(CallbackInfo ci) {
        potatoTechKit$clock.swapPhase(GamePhase.VILLAGE_SIEGE);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 6))
    public void afterVillageSiege(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;worldTeleporter:Lnet/minecraft/world/Teleporter;", opcode = Opcodes.GETFIELD))
    public void beforePortalRemoval(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.PORTAL_REMOVAL);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 7))
    public void afterPortalRemoval(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "sendQueuedBlockEvents", at = @At(value = "HEAD"))
    public void beforeBlockEvent(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.BLOCK_EVENT);
    }

    @Inject(method = "sendQueuedBlockEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;blockEventCacheIndex:I", opcode = Opcodes.GETFIELD, ordinal = 1))
    public void blockEventPush(CallbackInfo ci) {
        // event depth++
        potatoTechKit$clock.pushSubPhase();
    }

    @Inject(method = "sendQueuedBlockEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;fireBlockEvent(Lnet/minecraft/block/BlockEventData;)Z"))
    public void blockEventSwap(CallbackInfo ci) {
        // ordinal++ at the depth
        potatoTechKit$clock.swapSubPhase();
    }

    @Inject(method = "sendQueuedBlockEvents", at = @At(value = "RETURN"))
    public void afterBlockEvent(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "updateEntities", at = @At(value = "HEAD"))
    public void beforeWorldIdleCheck(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.WORLD_IDLE_CHECK);
    }

    @Inject(method = "updateEntities", at = {@At(value = "RETURN", ordinal = 0), @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;provider:Lnet/minecraft/world/WorldProvider;")})
    public void afterWorldIdleCheck(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }

    @Inject(method = "tickPlayers", at = @At(value = "HEAD"))
    public void beforePlayerEntities(CallbackInfo ci) {
        potatoTechKit$clock.pushPhase(GamePhase.PLAYER_UPDATE);
    }

    @Inject(method = "tickPlayers", at = @At(value = "RETURN"))
    public void afterPlayerEntities(CallbackInfo ci) {
        potatoTechKit$clock.popPhase();
    }
}
