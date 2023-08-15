package io.github.rainyaphthyl.potteckit;

import io.github.rainyaphthyl.potteckit.util.ImmutableSetView;
import io.github.rainyaphthyl.potteckit.util.MonoPriorGraph;
import net.minecraft.util.math.ChunkPos;

public class GraphTest {
    public static void main(String[] args) {
        MonoPriorGraph<ChunkPos, String> graph = new MonoPriorGraph<>(ChunkPos.class, String.class);
        ChunkPos[] nodes = new ChunkPos[]{
                new ChunkPos(0, 0),
                new ChunkPos(0, 1),
                new ChunkPos(1, 1),
                new ChunkPos(1, 0)
        };
        for (ChunkPos pos : nodes) {
            graph.addNode(pos);
        }
        graph.putEdgeValue(nodes[0], nodes[1], "Edge<0,1>");
        ImmutableSetView<ChunkPos> nodeSet = graph.nodes();
        for (ChunkPos pos : nodeSet) {
            System.out.println(pos);
        }
    }
}
