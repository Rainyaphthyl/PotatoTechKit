package io.github.rainyaphthyl.potteckit.mixin.meters;

import io.github.rainyaphthyl.potteckit.calculator.AnvilAssistant;
import net.minecraft.client.gui.GuiRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRepair.class)
public abstract class MixinGuiRepair {
    @Inject(method = "onGuiClosed", at = @At(value = "RETURN"))
    public void clearOnClosed(CallbackInfo ci) {
        AnvilAssistant.slotsToEnchant.clear();
    }
}
