package io.github.rainyaphthyl.potteckit.mixin.meters;

import io.github.rainyaphthyl.potteckit.entities.ArrowSimulator;
import io.github.rainyaphthyl.potteckit.entities.Renderers;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBow.class)
public abstract class MixinItemBow extends Item {
    @Inject(method = "onItemRightClick", at = @At(value = "RETURN", ordinal = 1))
    public void onStartCharging(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        if (worldIn instanceof WorldClient) {
            ArrowSimulator simulator = new ArrowSimulator(playerIn, (WorldClient) worldIn);
            Renderers.PROJECTILE_AIM_RENDERER.aimListMap.clear();
            Renderers.PROJECTILE_AIM_RENDERER.aimDamageMap.clear();
            simulator.predictDestination(3.0F, 1.0F);
            Renderers.PROJECTILE_AIM_RENDERER.updateCounter.incrementAndGet();
        }
    }

    @Inject(method = "onPlayerStoppedUsing", at = @At(value = "RETURN"))
    public void onDispenseArrow(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft, CallbackInfo ci) {
        if (worldIn instanceof WorldClient) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                } finally {
                    int count = Renderers.PROJECTILE_AIM_RENDERER.updateCounter.decrementAndGet();
                    if (count <= 0) {
                        Renderers.PROJECTILE_AIM_RENDERER.aimListMap.clear();
                        Renderers.PROJECTILE_AIM_RENDERER.aimDamageMap.clear();
                    }
                }
            }, "Projectile Aim");
            thread.setDaemon(true);
            thread.start();
        }
    }
}
