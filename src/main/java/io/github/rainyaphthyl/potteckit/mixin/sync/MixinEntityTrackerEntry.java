package io.github.rainyaphthyl.potteckit.mixin.sync;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry {
    @Shadow
    @Final
    private int range;

    @Redirect(method = "isVisibleTo", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/EntityTrackerEntry;range:I"))
    public int onCheckTrackerDistance(EntityTrackerEntry instance) {
        if (Configs.entityTrackerDistance.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            return Configs.entityTrackerDistance.getIntegerValue() * 16;
        } else {
            return range;
        }
    }
}
