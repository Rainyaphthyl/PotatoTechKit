package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkLoadCaptor;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = "handleCustomPayload", at = @At(value = "RETURN"))
    public void onHandleCustomPayload(@Nonnull SPacketCustomPayload packetIn, CallbackInfo ci) {
        String channelName = packetIn.getChannelName();
        switch (channelName) {
            case ChunkLoadCaptor.CHANNEL_EVENT: {
                PacketBuffer bufferData = packetIn.getBufferData();
                break;
            }
            default:
        }
    }
}
