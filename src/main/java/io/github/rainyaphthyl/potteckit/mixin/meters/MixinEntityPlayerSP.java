package io.github.rainyaphthyl.potteckit.mixin.meters;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.entities.ArrowSimulator;
import io.github.rainyaphthyl.potteckit.entities.EntityAimCamera;
import io.github.rainyaphthyl.potteckit.entities.Renderers;
import io.github.rainyaphthyl.potteckit.entities.ThrowableSimulator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "isCurrentViewEntity", at = @At(value = "RETURN"), cancellable = true)
    public void checkCurrentSlaveCamera(@Nonnull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            Entity viewEntity = mc.getRenderViewEntity();
            if (viewEntity instanceof EntityAimCamera) {
                EntityAimCamera camera = (EntityAimCamera) viewEntity;
                boolean flag = camera.isCurrentShooter(this);
                if (flag && cir.isCancellable() && !cir.isCancelled()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    public void checkBowUsage(CallbackInfo ci) {
        boolean flag = true;
        if (Configs.projectileAimIndicator.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (Configs.projectileAimTrigger.isHeld()) {
                ItemStack itemStack = getHeldItemMainhand();
                Item item = itemStack.getItem();
                ImmutableList<Item> immutableList = Configs.projectileAimList.getValue();
                boolean hasItem = immutableList.contains(item);
                if (!hasItem) {
                    itemStack = getHeldItemOffhand();
                    item = itemStack.getItem();
                    hasItem = immutableList.contains(item);
                }
                if (hasItem) {
                    boolean arrow = Items.BOW == item;
                    boolean normal = Items.EGG == item || Items.ENDER_PEARL == item || Items.SNOWBALL == item;
                    boolean potion = Items.SPLASH_POTION == item || Items.LINGERING_POTION == item;
                    boolean bottle = Items.EXPERIENCE_BOTTLE == item;
                    if (arrow) {
                        ArrowSimulator simulator = new ArrowSimulator(this, (WorldClient) world);
                        simulator.predictDestination(0.0F, 3.0F, 1.0F);
                        flag = false;
                    } else if (normal || potion || bottle) {
                        ThrowableSimulator simulator = new ThrowableSimulator(this, (WorldClient) world, item);
                        if (normal) {
                            simulator.predictDestination(0.0F, 1.5F, 1.0F);
                        } else if (bottle) {
                            simulator.predictDestination(-20.0F, 0.7F, 1.0F);
                        } else {
                            simulator.predictDestination(-20.0F, 0.5F, 1.0F);
                        }
                        flag = false;
                    }
                }
            }
        }
        if (flag) {
            EntityAimCamera.removeAimCamera();
            Renderers.PROJECTILE_AIM_RENDERER.removeAimRange();
        }
    }
}
