package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.gen.structure.MapGenEndCity;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenEndCity.Start.class)
public abstract class MixinMapGenEndCity$Start extends StructureStart {
    @Unique
    private boolean potatoTechKit$needsReport = false;
    @Shadow
    private boolean isSizeable;

    @Inject(method = "<init>()V", at = @At(value = "RETURN"))
    public void onEmptyConstruct(CallbackInfo ci) {
        if (Configs.logInvalidEndCity.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$needsReport = true;
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            if (server != null) {
                String message = "\u00A7eAn End City becomes invalid...\u00A7r";
                server.getPlayerList().sendMessage(new TextComponentString(message), true);
            }
        }
    }

    @Inject(method = "isSizeableStructure", at = @At(value = "RETURN"))
    public void onValidityQuery(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.logInvalidEndCity.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if (potatoTechKit$needsReport && !isSizeable) {
                IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
                if (server != null) {
                    int cx = getChunkPosX();
                    int cz = getChunkPosZ();
                    String message = "\u00A7eThe End City starting at chunk [" + cx + ", " + cz + "] is invalid!\u00A7r";
                    server.getPlayerList().sendMessage(new TextComponentString(message), true);
                    potatoTechKit$needsReport = false;
                }
            }
        }
    }
}
