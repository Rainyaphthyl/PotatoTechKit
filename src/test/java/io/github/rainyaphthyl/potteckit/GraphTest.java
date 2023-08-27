package io.github.rainyaphthyl.potteckit;

import io.github.rainyaphthyl.potteckit.util.ImmutableSetView;
import io.github.rainyaphthyl.potteckit.util.NetworkGraph;
import net.minecraft.util.math.ChunkPos;

public class GraphTest {
    public static void main(String[] args) {
        NetworkGraph<ChunkPos, String> graph = new NetworkGraph<>(ChunkPos.class, String.class);
        ChunkPos[] nodes = new ChunkPos[]{
                new ChunkPos(0, 0),
                new ChunkPos(0, 1),
                new ChunkPos(1, 1),
                new ChunkPos(1, 0)
        };
        for (ChunkPos pos : nodes) {
            graph.addNode(pos);
        }
        ImmutableSetView<ChunkPos> nodeSet = graph.nodes();
        for (ChunkPos pos : nodeSet) {
            System.out.println(pos);
        }
    }
}
