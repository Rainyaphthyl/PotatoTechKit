package io.github.rainyaphthyl.potteckit.mixin.optifix;

import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
    @Inject(method = "preparePlayer", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerChunkMap;addPlayer(Lnet/minecraft/entity/player/EntityPlayerMP;)V"))
    public void onPreparePlayer(EntityPlayerMP playerIn, WorldServer worldIn, CallbackInfo ci, WorldServer worldserver) {
        if (Configs.optifineJoiningGameDebug.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            Iterator<Chunk> chunkIterator = worldserver.getPlayerChunkMap().getChunkIterator();
            final int[] count = {0};
            chunkIterator.forEachRemaining(chunk -> ++count[0]);
            Reference.LOGGER.info("Player Chunk Map Size = {} before", count[0]);
            Reference.LOGGER.info("Adding player {}", playerIn);
        }
    }

    @Inject(method = "preparePlayer", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "RETURN"))
    public void afterPreparePlayer(EntityPlayerMP playerIn, WorldServer worldIn, CallbackInfo ci, WorldServer worldserver) {
        if (Configs.optifineJoiningGameDebug.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            Iterator<Chunk> chunkIterator = worldserver.getPlayerChunkMap().getChunkIterator();
            final int[] count = {0};
            chunkIterator.forEachRemaining(chunk -> ++count[0]);
            Reference.LOGGER.info("Player Chunk Map Size = {} after", count[0]);
        }
    }
}
