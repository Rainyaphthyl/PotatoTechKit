package io.github.rainyaphthyl.potteckit.entities;

import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AimRangePacket implements Iterable<Double2ObjectMap.Entry<AimRangePacket.Range>> {
    private final Double2ObjectSortedMap<Range> vertexCircleMap = new Double2ObjectAVLTreeMap<>();
    private boolean completed = false;

    {
        vertexCircleMap.defaultReturnValue(null);
    }

    public AimRangePacket() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AimRangePacket)) return false;
        AimRangePacket that = (AimRangePacket) obj;
        if (!completed || !that.completed) return false;
        return super.equals(obj);
    }

    public void addVertexAtLevel(double level, Vec3d position, boolean atEntity) {
        Vertex vertex = new Vertex(position, atEntity);
        double absLevel = Math.abs(level);
        addVertexAtLevel(absLevel, vertex);
    }

    public void setCompleted() {
        synchronized (vertexCircleMap) {
            if (completed) return;
            for (Double2ObjectMap.Entry<Range> entry : vertexCircleMap.double2ObjectEntrySet()) {
                Range range = entry.getValue();
                range.setCompleted();
            }
            completed = true;
        }
    }

    public void addVertexAtLevel(double level, Vertex vertex) {
        if (completed) {
            return;
        }
        synchronized (vertexCircleMap) {
            Range range = vertexCircleMap.get(level);
            if (range == null) {
                range = new Range();
                vertexCircleMap.put(level, range);
            }
            range.add(vertex);
        }
    }

    @Override
    @Nonnull
    public Iterator<Double2ObjectMap.Entry<Range>> iterator() {
        return vertexCircleMap.double2ObjectEntrySet().iterator();
    }

    public static class Range implements Iterable<Vertex> {
        private final List<Vertex> vertexList = new ArrayList<>();
        private boolean atEntity = true;
        private boolean completed = false;

        public Range() {
        }

        @Override
        @Nonnull
        public Iterator<Vertex> iterator() {
            return vertexList.listIterator();
        }

        public void setCompleted() {
            completed = true;
        }

        public void add(Vertex vertex) {
            if (completed) {
                return;
            }
            if (vertex != null && vertex.position != null) {
                synchronized (vertexList) {
                    atEntity &= vertex.atEntity;
                    vertexList.add(vertex);
                }
            }
        }

        public int size() {
            synchronized (vertexList) {
                return vertexList.size();
            }
        }

        public boolean isAtEntity() {
            return atEntity && size() > 0;
        }
    }

    public static class Vertex {
        public final Vec3d position;
        public final boolean atEntity;

        public Vertex(Vec3d position, boolean atEntity) {
            this.position = position;
            this.atEntity = atEntity;
        }
    }
}
