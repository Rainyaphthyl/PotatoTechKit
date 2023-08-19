package io.github.rainyaphthyl.potteckit.mixin.client.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
    @Unique
    private final Minecraft potatoTechKit$client = Minecraft.getMinecraft();
    @Unique
    public final Profiler potatoTechKit$profiler = potatoTechKit$client.profiler;
    @Shadow
    @Final
    private BlockModelRenderer blockModelRenderer;
    @Shadow
    @Final
    private BlockFluidRenderer fluidRenderer;

    @Shadow
    public abstract IBakedModel getModelForState(IBlockState state);

    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getActualState(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState profileGetBlockState(IBlockState instance, IBlockAccess blockAccess, BlockPos pos) {
        if (potatoTechKit$client.isCallingFromMinecraftThread() && Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("getActualState");
            IBlockState actualState = instance.getActualState(blockAccess, pos);
            potatoTechKit$profiler.endSection();
            return actualState;
        } else {
            return instance.getActualState(blockAccess, pos);
        }
    }

    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;getModelForState(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/client/renderer/block/model/IBakedModel;"))
    public IBakedModel profileGetModel(BlockRendererDispatcher instance, IBlockState state) {
        if (potatoTechKit$client.isCallingFromMinecraftThread() && Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("getModel");
            IBakedModel model = instance.getModelForState(state);
            potatoTechKit$profiler.endSection();
            return model;
        } else {
            return instance.getModelForState(state);
        }
    }

    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z"))
    public boolean profileRenderModel(BlockModelRenderer instance, IBlockAccess blockAccess, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides) {
        if (potatoTechKit$client.isCallingFromMinecraftThread() && Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("renderModel");
            boolean result = instance.renderModel(blockAccess, model, state, pos, buffer, checkSides);
            potatoTechKit$profiler.endSection();
            return result;
        } else {
            return instance.renderModel(blockAccess, model, state, pos, buffer, checkSides);
        }
    }

    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockFluidRenderer;renderFluid(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;)Z"))
    public boolean profileRenderFluid(BlockFluidRenderer instance, IBlockAccess blockAccess, IBlockState state, BlockPos pos, BufferBuilder buffer) {
        if (potatoTechKit$client.isCallingFromMinecraftThread() && Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            potatoTechKit$profiler.startSection("renderFluid");
            boolean result = instance.renderFluid(blockAccess, state, pos, buffer);
            potatoTechKit$profiler.endSection();
            return result;
        } else {
            return instance.renderFluid(blockAccess, state, pos, buffer);
        }
    }

    ///////////////////
    // Optimizations //
    ///////////////////

    @Inject(method = "renderBlock", at = @At(value = "HEAD"), cancellable = true)
    public void redirectRenderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.optimizeChunkRenderer.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            try {
                EnumBlockRenderType renderType = state.getRenderType();
                switch (renderType) {
                    case INVISIBLE:
                    case ENTITYBLOCK_ANIMATED:
                        cir.setReturnValue(false);
                        return;
                }
                IBlockState oldState = state;
                if (blockAccess.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
                    try {
                        state = oldState.getActualState(blockAccess, pos);
                    } catch (Exception ignored) {
                    }
                }
                boolean result;
                switch (renderType) {
                    case MODEL:
                        IBakedModel actualModel = getModelForState(state);
                        IBakedModel oldModel = getModelForState(oldState);
                        if (oldModel == actualModel) {
                            result = false;
                        } else {
                            result = blockModelRenderer.renderModel(blockAccess, actualModel, state, pos, bufferBuilderIn, true);
                        }
                        break;
                    case LIQUID:
                        result = fluidRenderer.renderFluid(blockAccess, state, pos, bufferBuilderIn);
                        break;
                    default:
                        result = false;
                }
                cir.setReturnValue(result);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
                throw new ReportedException(crashreport);
            }
        }
    }
}
