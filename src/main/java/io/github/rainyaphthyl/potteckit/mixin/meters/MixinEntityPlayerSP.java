package io.github.rainyaphthyl.potteckit.mixin.meters;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.entities.ArrowSimulator;
import io.github.rainyaphthyl.potteckit.entities.EntityAimCamera;
import io.github.rainyaphthyl.potteckit.entities.Renderers;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    public void checkBowUsage(CallbackInfo ci) {
        boolean flag = true;
        if (Configs.projectileAimIndicator.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (Configs.projectileAimTrigger.getKeyBind().isKeyBindHeld()) {
                ItemStack itemStack = getHeldItemMainhand();
                Item item = itemStack.getItem();
                ImmutableList<Item> itemList = Configs.projectileAimList.getValue();
                if (itemList.contains(item)) {
                    ArrowSimulator simulator = new ArrowSimulator(this, (WorldClient) world);
                    Renderers.PROJECTILE_AIM_RENDERER.aimListMap.clear();
                    Renderers.PROJECTILE_AIM_RENDERER.aimDamageMap.clear();
                    simulator.predictDestination(3.0F, 1.0F);
                    flag = false;
                }
            }
        }
        if (flag) {
            Renderers.PROJECTILE_AIM_RENDERER.aimListMap.clear();
            Renderers.PROJECTILE_AIM_RENDERER.aimDamageMap.clear();
            EntityAimCamera.removeAimCamera();
        }
    }
}
