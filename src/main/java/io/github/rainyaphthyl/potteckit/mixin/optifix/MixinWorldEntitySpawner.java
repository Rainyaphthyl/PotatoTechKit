package io.github.rainyaphthyl.potteckit.mixin.optifix;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(WorldEntitySpawner.class)
public abstract class MixinWorldEntitySpawner {
    @Unique
    private final AtomicReference<WorldServer> potatoTechKit$worldServer = new AtomicReference<>(null);
    /**
     * Eligible Chunk Set
     */
    @Unique
    private final AtomicBoolean potatoTechKit$ecsRecheckFlag = new AtomicBoolean(false);
    @Shadow
    @Final
    private Set<ChunkPos> eligibleChunksForSpawning;

    @Inject(method = "findChunksForSpawning", at = @At(value = "HEAD"))
    public void captureWorldServer(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir) {
        potatoTechKit$worldServer.weakCompareAndSet(null, worldServerIn);
        potatoTechKit$ecsRecheckFlag.weakCompareAndSet(true, false);
    }

    /**
     * @param value {@code i}
     * @return {@code i}
     */
    @ModifyVariable(method = "findChunksForSpawning", ordinal = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EnumCreatureType;values()[Lnet/minecraft/entity/EnumCreatureType;"))
    public int forceUpdateEligibleChunks(int value) {
        if (Configs.optifineSpawningFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            WorldServer worldServerIn = potatoTechKit$worldServer.get();
            if (!potatoTechKit$ecsRecheckFlag.get() && worldServerIn != null) {
                int count = 0;
                eligibleChunksForSpawning.clear();
                for (EntityPlayer player : worldServerIn.playerEntities) {
                    if (!player.isSpectator()) {
                        int px = MathHelper.floor(player.posX / 16.0D);
                        int pz = MathHelper.floor(player.posZ / 16.0D);
                        for (int cx = -8; cx <= 8; ++cx) {
                            for (int cz = -8; cz <= 8; ++cz) {
                                boolean flag = cx == -8 || cx == 8 || cz == -8 || cz == 8;
                                ChunkPos chunkPos = new ChunkPos(cx + px, cz + pz);
                                if (!eligibleChunksForSpawning.contains(chunkPos)) {
                                    ++count;
                                    if (!flag && worldServerIn.getWorldBorder().contains(chunkPos)) {
                                        PlayerChunkMapEntry pcmEntry = worldServerIn.getPlayerChunkMap().getEntry(chunkPos.x, chunkPos.z);
                                        if (pcmEntry != null && pcmEntry.isSentToPlayers()) {
                                            eligibleChunksForSpawning.add(chunkPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                value = count;
            }
        }
        return value;
    }

    @Inject(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0))
    public void captureChunkRegInit(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir) {
        if (Configs.optifineSpawningFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$ecsRecheckFlag.weakCompareAndSet(false, true);
        }
    }
}
