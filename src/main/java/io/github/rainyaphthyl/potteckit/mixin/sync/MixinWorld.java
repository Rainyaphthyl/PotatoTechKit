package io.github.rainyaphthyl.potteckit.mixin.sync;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess {
    @Unique
    private final ConcurrentMap<Thread, Boolean> potatoTechKit$flagRegister = new ConcurrentHashMap<>();
    @Shadow
    @Final
    public boolean isRemote;

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getLightOpacity()I", ordinal = 0))
    public void captureBlock(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir, Chunk chunk, Block block, IBlockState iblockstate) {
        // This only works on server
        if (!isRemote && Configs.blockStateTextureSync.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            if ((flags & 0x2) == 0x0 && block != null) {
                List<Block> blockSyncSet = Configs.blockStateTextureSyncList.getValue();
                if (blockSyncSet != null && blockSyncSet.contains(block)) {
                    potatoTechKit$flagRegister.put(Thread.currentThread(), Boolean.TRUE);
                }
            }
        }
    }

    @ModifyVariable(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getLightOpacity()I", ordinal = 0), argsOnly = true)
    public int resetUpdateFlags(int flags) {
        if (!isRemote && potatoTechKit$flagRegister.remove(Thread.currentThread(), Boolean.TRUE)) {
            return flags | 0x2;
        } else {
            return flags;
        }
    }
}
