package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.config.option.EnumRealmStatus;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu {
    @Shadow
    private GuiButton realmsButton;

    @Inject(method = "addSingleplayerMultiplayerButtons", at = @At(value = "RETURN"))
    public void yeetRealmButton(int y1, int y2, CallbackInfo ci) {
        EnumRealmStatus value = Configs.enablePotteckit.getValue() ? Configs.yeetRealmPage.getValue() : EnumRealmStatus.VANILLA;
        switch (value) {
            case INVISIBLE:
                realmsButton.visible = false;
            case DISABLED:
                realmsButton.enabled = false;
                break;
        }
    }
}
