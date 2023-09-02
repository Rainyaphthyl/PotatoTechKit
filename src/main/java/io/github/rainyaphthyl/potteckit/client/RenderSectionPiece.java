package io.github.rainyaphthyl.potteckit.client;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.World;

public class RenderSectionPiece extends RenderChunk {
    public RenderSectionPiece(World worldIn, RenderGlobal renderGlobalIn, int indexIn) {
        super(worldIn, renderGlobalIn, indexIn);
    }
}
