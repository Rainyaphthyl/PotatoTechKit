package io.github.rainyaphthyl.potteckit.mixin.core;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo {
    @Shadow
    @Final
    private Session session;
    @Shadow
    @Final
    private PropertyMap profileProperties;

    @Shadow
    public abstract MinecraftSessionService getSessionService();

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;initiateShutdown()V"))
    public void skipDuplicateShutdown(@Nonnull IntegratedServer serverIn) {
        boolean flag = Configs.fixLanQuittingFreeze.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (!flag) {
            serverIn.initiateShutdown();
        }
    }

    @Inject(method = "getProfileProperties", at = @At(value = "HEAD"))
    public void addSignature(CallbackInfoReturnable<PropertyMap> cir) {
        boolean flag = Configs.fixLanSkinAbsence.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (flag) {
            if (profileProperties.isEmpty()) {
                GameProfile profile = getSessionService().fillProfileProperties(session.getProfile(), true);
                profileProperties.putAll(profile.getProperties());
            }
        }
    }
}
