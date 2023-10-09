package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(PlayerChunkMap.class)
public abstract class MixinPlayerChunkMap {
    @Shadow
    private int playerViewRadius;
    @Shadow
    @Final
    private List<EntityPlayerMP> players;

    @Shadow
    protected abstract PlayerChunkMapEntry getOrCreateEntry(int chunkX, int chunkZ);

    @Shadow
    protected abstract void markSortPending();

    /**
     * Rewrite it before OptiFine destroys it
     */
    @Inject(method = "addPlayer", at = @At(value = "HEAD"), cancellable = true)
    public void forceVanillaAddPlayer(@Nonnull EntityPlayerMP player, CallbackInfo ci) {
        if (Configs.optifineJoiningGameFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            final int playerCX = (int) player.posX >> 4;
            final int playerCZ = (int) player.posZ >> 4;
            player.managedPosX = player.posX;
            player.managedPosZ = player.posZ;
            final int radius = playerViewRadius;
            for (int cx = playerCX - radius; cx <= playerCX + radius; ++cx) {
                for (int cz = playerCZ - radius; cz <= playerCZ + radius; ++cz) {
                    getOrCreateEntry(cx, cz).addPlayer(player);
                }
            }
            players.add(player);
            markSortPending();
            if (ci.isCancellable() && !ci.isCancelled()) {
                ci.cancel();
            }
        }
    }
}
