package io.github.rainyaphthyl.potteckit.util;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
public class MonoPriorGraph<N, V> implements MutableValueGraph<N, V> {
    private final MutableValueGraph<Object, Object> graph;
    private final Class<? extends N> nodeClass;
    private final Class<? extends V> valueClass;

    {
        ValueGraphBuilder<Object, Object> builder = ValueGraphBuilder.directed().nodeOrder(ElementOrder.insertion());
        builder.allowsSelfLoops(true);
        graph = builder.build();
    }

    public MonoPriorGraph(Class<? extends N> nodeClass, Class<? extends V> valueClass) {
        this.nodeClass = nodeClass;
        this.valueClass = valueClass;
    }

    @Override
    public boolean addNode(N node) {
        return graph.addNode(node);
    }

    @Override
    public V putEdgeValue(N nodeU, N nodeV, V value) throws ClassCastException {
        Object object = graph.putEdgeValue(nodeU, nodeV, value);
        return valueClass.cast(object);
    }

    @Override
    public boolean removeNode(Object node) {
        return graph.removeNode(node);
    }

    @Override
    public V removeEdge(Object nodeU, Object nodeV) throws ClassCastException {
        Object object = graph.removeEdge(nodeU, nodeV);
        return valueClass.cast(object);
    }

    @Override
    public V edgeValue(Object nodeU, Object nodeV) throws ClassCastException {
        return edgeValueOrDefault(nodeU, nodeV, null);
    }

    @Override
    public V edgeValueOrDefault(Object nodeU, Object nodeV, @Nullable V defaultValue) throws ClassCastException {
        Object object = graph.edgeValueOrDefault(nodeU, nodeV, defaultValue);
        return valueClass.cast(object);
    }

    @Override
    public ImmutableSetView<N> nodes() {
        return new ImmutableSetView<>(graph.nodes(), nodeClass);
    }

    @Override
    @Deprecated
    public ImmutableSetView<EndpointPair<N>> edges() {
        Set<EndpointPair<Object>> objPairSet = graph.edges();
        return null;
    }

    @Override
    public boolean isDirected() {
        return graph.isDirected();
    }

    @Override
    public boolean allowsSelfLoops() {
        return graph.allowsSelfLoops();
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return ElementOrder.insertion();
    }

    @Override
    public ImmutableSetView<N> adjacentNodes(Object node) {
        Set<Object> objectSet = graph.adjacentNodes(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public Set<N> predecessors(Object node) {
        Set<Object> objectSet = graph.predecessors(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public Set<N> successors(Object node) {
        Set<Object> objectSet = graph.successors(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public int degree(Object node) {
        return graph.degree(node);
    }

    @Override
    public int inDegree(Object node) {
        return graph.inDegree(node);
    }

    @Override
    public int outDegree(Object node) {
        return graph.outDegree(node);
    }
}
