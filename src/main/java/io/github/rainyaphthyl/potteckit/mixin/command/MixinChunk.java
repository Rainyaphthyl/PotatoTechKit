package io.github.rainyaphthyl.potteckit.mixin.command;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(Chunk.class)
public abstract class MixinChunk {
    @Redirect(method = "setBlockState", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/World;isRemote:Z"))
    public boolean skipBlockUpdate(@Nonnull World instance) {
        return instance.isRemote;
    }
}
