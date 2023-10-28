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
    public double modifyBlockEventRange(double constant) {
        if (Configs.blockEventPacketRange.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            return Configs.blockEventPacketRange.getDoubleValue();
        } else {
            return constant;
        }
    }

    @ModifyConstant(method = "newExplosion", constant = @Constant(doubleValue = 4096.0))
    public double modifyExplosionRangeSq(double constant) {
        if (Configs.explosionPacketRange.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            double range = Configs.explosionPacketRange.getDoubleValue();
            return range * range;
        } else {
            return constant;
        }
    }
}
