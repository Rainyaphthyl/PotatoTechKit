package io.github.rainyaphthyl.potteckit.mixin.meters;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "renderAttackIndicator", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z", ordinal = 0), cancellable = true)
    public void onRenderCrosshair(float partialTicks, ScaledResolution resolution, CallbackInfo ci, GameSettings gamesettings, int width, int height) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.f3CursorAttackIndicator.getBooleanValue()) {
            Entity entity = mc.getRenderViewEntity();
            if (entity != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (width / 2), (float) (height / 2), zLevel);
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(-1.0F, -1.0F, -1.0F);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();
                //drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
                if (mc.gameSettings.attackIndicator == 1) {
                    float f = mc.player.getCooledAttackStrength(0.0F);
                    boolean flag = false;
                    if (mc.pointedEntity != null && mc.pointedEntity instanceof EntityLivingBase && f >= 1.0F) {
                        flag = mc.player.getCooldownPeriod() > 5.0F;
                        flag = flag & mc.pointedEntity.isEntityAlive();
                    }
                    int i = height / 2 - 7 + 16;
                    int j = width / 2 - 8;
                    if (flag) {
                        drawTexturedModalRect(j, i, 68, 94, 16, 16);
                    } else if (f < 1.0F) {
                        int k = (int) (f * 17.0F);
                        drawTexturedModalRect(j, i, 36, 94, 16, 4);
                        drawTexturedModalRect(j, i, 52, 94, k, 4);
                    }
                }
                if (ci.isCancellable() && !ci.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }
}
