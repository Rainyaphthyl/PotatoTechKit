package io.github.rainyaphthyl.potteckit.mixin.render;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderChunk.class)
public abstract class MixinRenderChunk {
    @Unique
    private final Minecraft potatoTechKit$client = Minecraft.getMinecraft();
    @Unique
    private final Profiler potatoTechKit$profiler = potatoTechKit$client.profiler;
    @Unique
    private boolean potatoTechKit$profiling = false;

    @Inject(method = "rebuildChunk", at = @At(value = "HEAD"))
    public void startPreparing(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (Configs.moreProfilerLevels.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
                potatoTechKit$profiler.startSection("prepare");
                potatoTechKit$profiling = true;
            } else if (potatoTechKit$profiling) {
                potatoTechKit$profiling = false;
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "RETURN", ordinal = 0))
    public void endPreparing(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endSection();
            }
        }
    }

    //@Inject(method = "rebuildChunk", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;worldView:Lnet/minecraft/world/ChunkCache;", ordinal = 0))
    @Inject(method = "rebuildChunk", at = @At(value = "NEW", target = "()Lnet/minecraft/client/renderer/chunk/VisGraph;"))
    public void swapRendering(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endStartSection("render");
            }
        }
    }

    //@Inject(method = "rebuildChunk", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;worldView:Lnet/minecraft/world/ChunkCache;", ordinal = 1))
    //@Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ChunkCache;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    //public void startGetBlock(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
    //    if (potatoTechKit$client.isCallingFromMinecraftThread()) {
    //        if (potatoTechKit$profiling) {
    //            potatoTechKit$profiler.startSection("getBlock");
    //        }
    //    }
    //}

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;isOpaqueCube()Z"))
    public void swapSetOpaque(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                //potatoTechKit$profiler.endStartSection("opaque");
                potatoTechKit$profiler.startSection("opaque");
            }
        }
    }

    // Fix optifine conflict
    //@Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasTileEntity()Z"))
    //public void swapTileEntity(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
    //    if (potatoTechKit$client.isCallingFromMinecraftThread()) {
    //        if (potatoTechKit$profiling) {
    //            potatoTechKit$profiler.endStartSection("tileEntity");
    //        }
    //    }
    //}

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderLayer()Lnet/minecraft/util/BlockRenderLayer;"))
    public void endTileEntity(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endSection();
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;getRegionRenderCacheBuilder()Lnet/minecraft/client/renderer/RegionRenderCacheBuilder;", ordinal = 0))
    public void startPreRender(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.startSection("preRender");
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z"))
    public void endPreRender(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endSection();
            }
        }
    }

    @Inject(method = "postRenderBlocks", at = @At(value = "HEAD"))
    public void startPostRender(BlockRenderLayer layer, float x, float y, float z, BufferBuilder bufferBuilderIn, CompiledChunk compiledChunkIn, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.startSection("postRender");
            }
        }
    }

    @Inject(method = "postRenderBlocks", at = @At(value = "RETURN"))
    public void endPostRender(BlockRenderLayer layer, float x, float y, float z, BufferBuilder bufferBuilderIn, CompiledChunk compiledChunkIn, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endSection();
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/VisGraph;computeVisibility()Lnet/minecraft/client/renderer/chunk/SetVisibility;"))
    public void swapVisibility(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endStartSection("visibility");
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;lockCompileTask:Ljava/util/concurrent/locks/ReentrantLock;", ordinal = 0))
    public void swapFinishing(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endStartSection("finish");
            }
        }
    }

    @Inject(method = "rebuildChunk", at = @At(value = "RETURN", ordinal = 1))
    public void endFinishing(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        if (potatoTechKit$client.isCallingFromMinecraftThread()) {
            if (potatoTechKit$profiling) {
                potatoTechKit$profiler.endSection();
            }
        }
    }
}
