package io.github.rainyaphthyl.potteckit.mixin.server;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadReason;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper extends TileEntity {
    @Inject(method = "updateHopper", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityHopper;transferItemsOut()Z"))
    public void onHopperPointOut(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            ChunkLoadSource source = new ChunkLoadSource(chunkX, chunkZ, ChunkLoadReason.HOPPER_POINTING);
            ChunkLoadCaptor.pushThreadSource(source);
        }
    }
}
