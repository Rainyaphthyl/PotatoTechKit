package io.github.rainyaphthyl.potteckit.mixin.gamephase;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.MutablePhaseClock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider {
    @Unique
    protected MutablePhaseClock potatoTechKit$clock = null;

    @Inject(method = "setWorld", at = @At(value = "RETURN"))
    public void setPhaseClock(World worldIn, CallbackInfo ci) {
        if (worldIn instanceof WorldServer) {
            MinecraftServer server = worldIn.getMinecraftServer();
            potatoTechKit$clock = MutablePhaseClock.instanceFromServer(server);
            Objects.requireNonNull(potatoTechKit$clock);
        }
    }
}
