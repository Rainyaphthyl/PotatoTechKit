package io.github.rainyaphthyl.potteckit.mixin.command;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.command.CommandClone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(CommandClone.class)
public abstract class MixinCommandClone {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 3, ordinal = 1))
    public int modifyBlockUpdateFlags(int constant) {
        boolean flag = Configs.yeetFillUpdate.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        return flag ? (~1 & constant) : constant;
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    public void forgetBlockUpdates(World instance, BlockPos pos, Block blockType, boolean updateObservers) {
        boolean flag = Configs.yeetFillUpdate.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        if (!flag) {
            instance.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
        }
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getPendingBlockUpdates(Lnet/minecraft/world/gen/structure/StructureBoundingBox;Z)Ljava/util/List;"))
    public List<NextTickListEntry> forgetTickPending(World instance, StructureBoundingBox structureBB, boolean remove) {
        boolean flag = Configs.yeetFillUpdate.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        return flag ? null : instance.getPendingBlockUpdates(structureBB, remove);
    }
}
