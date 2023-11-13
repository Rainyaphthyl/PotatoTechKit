package io.github.rainyaphthyl.potteckit.mixin.command;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSetBlock.class)
public abstract class MixinCommandSetBlock {
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    public boolean modifyBlockUpdateFlags(World instance, BlockPos pos, IBlockState newState, int flags) {
        boolean flag = Configs.yeetFillUpdate.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (flag) {
            flags &= ~0x1;
            // 16 & 128
            // 0x10 & 0x80
            flags |= 0x90;
        }
        return instance.setBlockState(pos, newState, flags);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    public void forgetBlockUpdates(World instance, BlockPos pos, Block blockType, boolean updateObservers) {
        boolean flag = Configs.yeetFillUpdate.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (!flag) {
            instance.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
        }
    }
}
