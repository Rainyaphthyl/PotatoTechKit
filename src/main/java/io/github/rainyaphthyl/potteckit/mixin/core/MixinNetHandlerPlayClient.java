package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkLoadCaptor;
import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkLoadGraph;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
    @Inject(method = "handleCustomPayload", at = @At(value = "RETURN"))
    public void onHandleCustomPayload(@Nonnull SPacketCustomPayload packetIn, CallbackInfo ci) {
        String channelName = packetIn.getChannelName();
        if (ChunkLoadCaptor.CHANNEL_EVENT.equals(channelName)) {
            ChunkLoadGraph.receiveChunkEventPacket(packetIn);
        }
    }
}
