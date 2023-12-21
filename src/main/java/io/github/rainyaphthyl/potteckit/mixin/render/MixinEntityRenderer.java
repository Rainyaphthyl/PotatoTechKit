package io.github.rainyaphthyl.potteckit.mixin.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {
    @Unique
    private int potatoTechKit$prevFrameRate = 120;
    @Unique
    private boolean potatoTechKit$reduced = false;
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onInit(@Nonnull Minecraft mcIn, IResourceManager resourceManagerIn, CallbackInfo ci) {
        potatoTechKit$prevFrameRate = mcIn.gameSettings.limitFramerate;
    }

    @Inject(method = "updateCameraAndRender", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", args = {"ldc=mouse"}))
    public void reduceFPSOnLoseFocus(float partialTicks, long nanoTime, CallbackInfo ci, boolean active) {
        if (Configs.dynamicFPS.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (potatoTechKit$reduced && active) {
                mc.gameSettings.limitFramerate = potatoTechKit$prevFrameRate;
                potatoTechKit$reduced = false;
            } else if (!active && !potatoTechKit$reduced) {
                int nextFPSLimit = Configs.dynamicFPS.getIntegerValue();
                int currFPSLimit = mc.gameSettings.limitFramerate;
                if (nextFPSLimit < currFPSLimit) {
                    potatoTechKit$prevFrameRate = currFPSLimit;
                    mc.gameSettings.limitFramerate = nextFPSLimit;
                    potatoTechKit$reduced = true;
                }
            }
        }
    }
}
