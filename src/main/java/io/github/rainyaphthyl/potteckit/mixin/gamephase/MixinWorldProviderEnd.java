package io.github.rainyaphthyl.potteckit.mixin.gamephase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import net.minecraft.world.WorldProviderEnd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProviderEnd.class)
public abstract class MixinWorldProviderEnd extends MixinWorldProvider {
    @Inject(method = "onWorldUpdateEntities", at = @At(value = "HEAD"))
    public void beforeDragonFight(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.pushPhase(GamePhase.DRAGON_FIGHT);
        }
    }

    @Inject(method = "onWorldUpdateEntities", at = @At(value = "RETURN"))
    public void afterDragonFight(CallbackInfo ci) {
        if (potatoTechKit$clock != null) {
            potatoTechKit$clock.popPhase();
        }
    }
}
