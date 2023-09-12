package io.github.rainyaphthyl.potteckit.mixin.optifix;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

@Mixin(WorldEntitySpawner.class)
public abstract class MixinWorldEntitySpawner {
    @Shadow
    @Final
    private Set<ChunkPos> eligibleChunksForSpawning;
    @Unique
    private int potatoTechKit$prevMapSize = -1;
    @Unique
    private int potatoTechKit$previousPCM = -1;

    @Inject(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0))
    public void captureChunkRegInit(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir) {
        if (Configs.optifineSpawningFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            MinecraftServer server = worldServerIn.getMinecraftServer();
            long time = worldServerIn.getTotalWorldTime();
            StringBuilder textBuilder = new StringBuilder("§");
            textBuilder.append((time & 0x1) == 0 ? '7' : 'f');
            textBuilder.append('[').append(time).append("]§r");
            if (server != null) {
                textBuilder.append(" §bEligible chunks are being added§r");
                SPacketChat packet = new SPacketChat(new TextComponentString(textBuilder.toString()), ChatType.SYSTEM);
                server.getPlayerList().sendPacketToAllPlayersInDimension(packet, worldServerIn.provider.getDimensionType().getId());
            }
        }
    }

    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerChunkMap;getEntry(II)Lnet/minecraft/server/management/PlayerChunkMapEntry;"))
    public PlayerChunkMapEntry captureChunkRegistry(@Nonnull PlayerChunkMap instance, int x, int z) {
        PlayerChunkMapEntry entry = instance.getEntry(x, z);
        if (Configs.optifineSpawningFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            WorldServer worldServerIn = instance.getWorldServer();
            MinecraftServer server = worldServerIn.getMinecraftServer();
            if (server != null) {
                boolean failed = false;
                StringBuilder textBuilder = new StringBuilder("§7[");
                textBuilder.append(worldServerIn.getTotalWorldTime()).append("]§r [");
                textBuilder.append(x).append(", ").append(z).append(']');
                if (entry == null) {
                    failed = true;
                    textBuilder.append(" §cFailed because entry is null§r");
                } else if (!entry.isSentToPlayers()) {
                    failed = true;
                    textBuilder.append(" §cFailed because entry is not sent to players§r");
                }
                if (failed) {
                    SPacketChat packet = new SPacketChat(new TextComponentString(textBuilder.toString()), ChatType.SYSTEM);
                    server.getPlayerList().sendPacketToAllPlayersInDimension(packet, worldServerIn.provider.getDimensionType().getId());
                }
            }
        }
        return entry;
    }

    @Inject(method = "findChunksForSpawning", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "RETURN"))
    public void captureEligibleChunks(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir) {
        if (Configs.optifineSpawningFix.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            int mapSize = eligibleChunksForSpawning.size();
            int pcmEntries = 0;
            Iterator<Chunk> chunkIterator = worldServerIn.getPlayerChunkMap().getChunkIterator();
            while (chunkIterator.hasNext()) {
                chunkIterator.next();
                ++pcmEntries;
            }
            boolean smChanged = potatoTechKit$prevMapSize != mapSize;
            //noinspection unused
            boolean pcmChanged = potatoTechKit$previousPCM != pcmEntries;
            if (smChanged) {
                potatoTechKit$prevMapSize = mapSize;
                potatoTechKit$previousPCM = pcmEntries;
                MinecraftServer server = worldServerIn.getMinecraftServer();
                if (server != null) {
                    String text = "§7[" + worldServerIn.getTotalWorldTime() + "] §eEligible Chunks: " + mapSize + "§a Player Chunk Map: " + pcmEntries + "§r";
                    SPacketChat packet = new SPacketChat(new TextComponentString(text), ChatType.SYSTEM);
                    server.getPlayerList().sendPacketToAllPlayersInDimension(packet, worldServerIn.provider.getDimensionType().getId());
                }
            }
        }
    }
}
