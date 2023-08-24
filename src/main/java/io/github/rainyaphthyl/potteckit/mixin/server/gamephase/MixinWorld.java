package io.github.rainyaphthyl.potteckit.mixin.server.gamephase;

import io.github.rainyaphthyl.potteckit.server.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.server.phaseclock.MutablePhaseClock;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.lib.Opcodes;
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
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.WEATHER_UPDATE);
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "RETURN"))
    public void afterWeather(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0))
    public void beforeGlobalEntities(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.GLOBAL_ENTITY_UPDATE);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 2))
    public void afterGlobalEntities(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;loadedEntityList:Ljava/util/List;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public void beforeEntityUnload(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.ENTITY_UNLOAD);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;tickPlayers()V"))
    public void afterEntityUnload(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 3))
    public void beforeEntityUpdate(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.ENTITY_UPDATE);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 8))
    public void afterEntityUpdate(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    public void beforeTileEntityUnload(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.TILE_ENTITY_UNLOAD);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;processingLoadedTiles:Z", opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void swapTileEntityUpdate(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.swapPhase(GamePhase.TILE_ENTITY_UPDATE);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 11))
    public void afterTileEntityUpdate(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;addedTileEntityList:Ljava/util/List;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public void beforeTileEntityPending(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.TILE_ENTITY_PENDING);
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;profiler:Lnet/minecraft/profiler/Profiler;", opcode = Opcodes.GETFIELD, ordinal = 12))
    public void afterTileEntityPending(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }
}
