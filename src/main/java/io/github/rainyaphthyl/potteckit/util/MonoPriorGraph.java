package io.github.rainyaphthyl.potteckit.util;

import com.google.common.graph.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
public class MonoPriorGraph<N, E> extends AbstractNetwork<N, E> implements MutableNetwork<N, E> {
    private final MutableNetwork<Object, Object> graph;
    private final Class<? extends N> nodeClass;
    private final Class<? extends E> edgeClass;

    {
        NetworkBuilder<Object, Object> builder = NetworkBuilder.directed().nodeOrder(ElementOrder.insertion()).edgeOrder(ElementOrder.insertion()).allowsSelfLoops(true).allowsParallelEdges(true);
        graph = builder.build();
    }

    public MonoPriorGraph(Class<? extends N> nodeClass, Class<? extends E> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
    }

    @Override
    public boolean addNode(N node) {
        return graph.addNode(node);
    }

    @Override
    public boolean addEdge(N nodeU, N nodeV, E edge) {
        return graph.addEdge(nodeU, nodeV, edge);
    }

    @Override
    public boolean removeNode(Object node) {
        return graph.removeNode(node);
    }

    @Override
    public boolean removeEdge(Object edge) {
        return graph.removeEdge(edge);
    }

    @Override
    public ImmutableSetView<N> nodes() {
        Set<Object> objectSet = graph.nodes();
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public ImmutableSetView<E> edges() {
        Set<Object> objectSet = graph.edges();
        return new ImmutableSetView<>(objectSet, edgeClass);
    }

    @Override
    public boolean isDirected() {
        return graph.isDirected();
    }

    @Override
    public boolean allowsParallelEdges() {
        return graph.allowsParallelEdges();
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
    public ElementOrder<E> edgeOrder() {
        return ElementOrder.insertion();
    }

    @Override
    public ImmutableSetView<N> adjacentNodes(Object node) {
        Set<Object> objectSet = graph.adjacentNodes(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public ImmutableSetView<N> predecessors(Object node) {
        Set<Object> objectSet = graph.predecessors(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public ImmutableSetView<N> successors(Object node) {
        Set<Object> objectSet = graph.successors(node);
        return new ImmutableSetView<>(objectSet, nodeClass);
    }

    @Override
    public ImmutableSetView<E> incidentEdges(Object node) {
        Set<Object> objectSet = graph.incidentEdges(node);
        return new ImmutableSetView<>(objectSet, edgeClass);
    }

    @Override
    public ImmutableSetView<E> inEdges(Object node) {
        Set<Object> objectSet = graph.inEdges(node);
        return new ImmutableSetView<>(objectSet, edgeClass);
    }

    @Override
    public ImmutableSetView<E> outEdges(Object node) {
        Set<Object> objectSet = graph.outEdges(node);
        return new ImmutableSetView<>(objectSet, edgeClass);
    }

    @Override
    public EndpointPair<N> incidentNodes(Object edge) {
        EndpointPair<Object> objectPair = graph.incidentNodes(edge);
        Object objectU = objectPair.nodeU();
        Object objectV = objectPair.nodeV();
        N source = nodeClass.cast(objectU);
        N target = nodeClass.cast(objectV);
        if (objectPair.isOrdered()) {
            return EndpointPair.ordered(source, target);
        } else {
            return EndpointPair.unordered(source, target);
        }
    }

    @Override
    public ImmutableSetView<E> edgesConnecting(Object nodeU, Object nodeV) {
        Set<Object> objectSet = graph.edgesConnecting(nodeU, nodeV);
        return new ImmutableSetView<>(objectSet, edgeClass);
    }
}
