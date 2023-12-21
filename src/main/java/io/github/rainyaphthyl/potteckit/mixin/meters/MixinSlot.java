package io.github.rainyaphthyl.potteckit.mixin.meters;

import io.github.rainyaphthyl.potteckit.calculator.AnvilAssistant;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class MixinSlot {
    @Inject(method = "onSlotChanged", at = @At(value = "RETURN"))
    public void removeOnChanged(CallbackInfo ci) {
        if (!AnvilAssistant.slotsToEnchant.isEmpty()) {
            AnvilAssistant.slotsToEnchant.remove((Slot) (Object) this);
        }
    }
}
