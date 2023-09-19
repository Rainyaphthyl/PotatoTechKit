package io.github.rainyaphthyl.potteckit.mixin.access;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface AccessWorld {
    @Invoker(value = "isOutsideBuildHeight")
    boolean invokeIsOutsideBuildHeight(BlockPos pos);
}
