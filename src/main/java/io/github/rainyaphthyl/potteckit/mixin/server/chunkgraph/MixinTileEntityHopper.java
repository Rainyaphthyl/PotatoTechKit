package io.github.rainyaphthyl.potteckit.mixin.server.chunkgraph;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.server.chunkgraph.ChunkLoadReason;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper extends TileEntity {
    @Inject(method = "updateHopper", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityHopper;transferItemsOut()Z"))
    public void onHopperPointOut(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.chunkLoadingGraph.getBooleanValue()) {
            if (world instanceof WorldServer) {
                int chunkX = pos.getX() >> 4;
                int chunkZ = pos.getZ() >> 4;
                ChunkLoadCaptor.pushThreadSource(chunkX, chunkZ, ChunkLoadReason.HOPPER_POINTING);
            }
        }
    }
}
