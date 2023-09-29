package io.github.rainyaphthyl.potteckit.mixin.chunkgraph;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(RegionFile.class)
public abstract class MixinRegionFile {
    @Shadow
    @Final
    private File fileName;

    @Inject(method = "write(II[BI)V", at = @At(value = "RETURN", ordinal = 0))
    public void onSaveStateReturn(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (Configs.saveStateLogger.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            if (server != null && server.isServerRunning()) {
                double percentage = length * 100.0 / 1044475.0;
                String message = "§eChunk [" + x + ", " + z + "] overflow with " + String.format("%.2f", percentage) + "%§r";
                server.getPlayerList().sendMessage(new TextComponentString(message), true);
            }
        }
    }
}
