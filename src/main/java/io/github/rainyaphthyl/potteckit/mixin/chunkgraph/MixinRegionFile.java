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
import java.util.regex.Pattern;

@Mixin(RegionFile.class)
public abstract class MixinRegionFile {
    @Shadow
    @Final
    private File fileName;

    @Inject(method = "write(II[BI)V", at = @At(value = "RETURN", ordinal = 0))
    public void onSaveStateReturn(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (Configs.logSaveState.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            if (server != null) {
                double percentage = length * 100.0 / 1044475.0;
                String regionName = fileName.getName();
                // "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca"
                if (Pattern.matches("r\\.-?\\d+\\.-?\\d+\\.mca", regionName)) {
                    regionName = regionName.substring(2, regionName.length() - 4);
                    String[] regionPos = regionName.split("\\.");
                    if (regionPos.length == 2) {
                        int regionX = Integer.parseInt(regionPos[0]);
                        int chunkX = (regionX << 5) + x;
                        int regionZ = Integer.parseInt(regionPos[1]);
                        int chunkZ = (regionZ << 5) + z;
                        String message = "§eChunk [" + chunkX + ", " + chunkZ + "] overflow with " + String.format("%.2f", percentage) + "%§r";
                        server.getPlayerList().sendMessage(new TextComponentString(message), true);
                    }
                }
            }
        }
    }
}
