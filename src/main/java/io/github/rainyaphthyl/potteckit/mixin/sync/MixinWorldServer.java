package io.github.rainyaphthyl.potteckit.mixin.sync;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements IThreadListener {
    protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @ModifyConstant(method = "sendQueuedBlockEvents", constant = @Constant(doubleValue = 64.0))
    public double onGetBlockEventRange(double constant) {
        if (Configs.blockEventPacketRange.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            return Configs.blockEventPacketRange.getDoubleValue();
        } else {
            return constant;
        }
    }
}
