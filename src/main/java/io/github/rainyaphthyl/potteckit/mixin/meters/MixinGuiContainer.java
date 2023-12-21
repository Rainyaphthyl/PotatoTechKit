package io.github.rainyaphthyl.potteckit.mixin.meters;

import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.inventory.InventoryScreenUtils;
import io.github.rainyaphthyl.potteckit.calculator.AnvilAssistant;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {
    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerBackgroundLayer(FII)V"))
    public void highlightAnvilSlot(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.anvilEnchantIndicator.getBooleanValue()) {
            GuiContainer gui = (GuiContainer) (Object) this;
            int guiX = InventoryScreenUtils.getGuiPosX(gui);
            int guiY = InventoryScreenUtils.getGuiPosY(gui);
            for (Slot slot : AnvilAssistant.slotsToEnchant) {
                int colorRGB = 0xFF0000;
                ShapeRenderUtils.renderOutlinedRectangle(guiX + slot.xPos, guiY + slot.yPos, 1f, 16, 16, colorRGB, colorRGB | 0xFF000000);
            }
            GlStateManager.enableTexture2D();
        }
    }
}
