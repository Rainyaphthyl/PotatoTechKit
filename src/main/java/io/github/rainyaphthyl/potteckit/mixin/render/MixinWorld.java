package io.github.rainyaphthyl.potteckit.mixin.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow
    @Final
    public boolean isRemote;

    @Inject(method = "checkLightFor", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", args = "ldc=checkedPosition < toCheckCount"), cancellable = true)
    public void yeetPositionsToCheck(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && this.isRemote && Configs.yeetTravelLightCheck.getBooleanValue()) {
            Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
            if (viewEntity != null && viewEntity.dimension == -1) {
                if (viewEntity.motionX * viewEntity.motionX + viewEntity.motionZ * viewEntity.motionZ + viewEntity.motionY * viewEntity.motionY >= 0.3 * 0.3) {
                    if (cir.isCancellable() && !cir.isCancelled()) {
                        cir.setReturnValue(true);
                    }
                } else {
                    Entity ridingEntity = viewEntity.getRidingEntity();
                    if (ridingEntity != null) {
                        if (ridingEntity.motionX * ridingEntity.motionX + ridingEntity.motionZ * ridingEntity.motionZ + ridingEntity.motionY * ridingEntity.motionY >= 0.3 * 0.3) {
                            if (cir.isCancellable() && !cir.isCancelled()) {
                                cir.setReturnValue(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
