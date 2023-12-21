package io.github.rainyaphthyl.potteckit.mixin.sync;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
    @Shadow
    private int itemDropThreshold;
    @Shadow
    private int chatSpamThresholdCount;

    @Inject(method = "processCreativeInventoryAction", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/network/NetHandlerPlayServer;player:Lnet/minecraft/entity/player/EntityPlayerMP;", ordinal = 7))
    public void onAddItemThreshold(CPacketCreativeInventoryAction packetIn, CallbackInfo ci) {
        if (Configs.yeetItemAntiSpam.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            itemDropThreshold = 0;
        }
    }

    @Inject(method = "processChatMessage", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/network/NetHandlerPlayServer;chatSpamThresholdCount:I", ordinal = 1))
    public void onCheckChatSpam(CPacketChatMessage packetIn, CallbackInfo ci) {
        if (Configs.yeetChatAntiSpam.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            chatSpamThresholdCount = 0;
        }
    }
}
