package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;initiateShutdown()V"))
    public void skipDuplicateShutdown(@Nonnull IntegratedServer serverIn) {
        boolean flag = Configs.fixLanQuittingFreeze.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (!flag) {
            serverIn.initiateShutdown();
        }
    }
}
