package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements ICommandSender {
    @Shadow
    private boolean invulnerable;

    @Inject(method = "isEntityInvulnerable", cancellable = true, at = @At(value = "HEAD"))
    public void onCheckInvulnerable(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.creativeInvulnerableCrystal.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (cir != null && cir.isCancellable() && !cir.isCancelled()) {
                boolean result = invulnerable && source != DamageSource.OUT_OF_WORLD;
                cir.setReturnValue(result);
            }
        }
    }
}
