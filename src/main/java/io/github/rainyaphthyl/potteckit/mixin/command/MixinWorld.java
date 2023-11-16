package io.github.rainyaphthyl.potteckit.mixin.command;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess {
    @Unique
    private boolean potatoTechKit$silent = false;

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", at = @At(value = "HEAD"))
    public void checkParamFlags(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        potatoTechKit$silent = (flags & 0x80) != 0;
    }

    @Redirect(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState conditionalUpdate(Chunk instance, BlockPos pos, IBlockState newState) {
        if (potatoTechKit$silent) {
            return instance.setBlockState(pos, newState);
        } else {
            return instance.setBlockState(pos, newState);
        }
    }
}
