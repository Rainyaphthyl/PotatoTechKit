package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MutablePhaseClock {
    private static final ConcurrentMap<MinecraftServer, MutablePhaseClock> serverClockPool = new ConcurrentHashMap<>();
    private final MinecraftServer server;
    private DimensionType dimension = null;
    private GamePhase phase = null;

    private MutablePhaseClock(MinecraftServer server) {
        this.server = Objects.requireNonNull(server);
    }

    public static MutablePhaseClock instanceFromServer(MinecraftServer server) {
        if (server == null) {
            return null;
        } else {
            return serverClockPool.computeIfAbsent(server, MutablePhaseClock::new);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutablePhaseClock)) return false;
        MutablePhaseClock that = (MutablePhaseClock) o;
        if (dimension != that.dimension) return false;
        return phase == that.phase;
    }

    public PhaseRecord toImmutable() {
        return PhaseRecord.getPooledRecord(dimension, phase);
    }

    public DimensionType getDimension() {
        return dimension;
    }

    public void setDimension(DimensionType dimension) {
        this.dimension = dimension;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void pushPhase(GamePhase phase) {
        if (this.phase == null && phase != null) {
            this.phase = phase;
            //region debug
            System.out.println("[" + server.getTickCounter() + "] Push phase in: " + phase);
            //endregion
        }
    }

    public void popPhase() {
        if (phase != null) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] Pop phase out: " + phase);
            //endregion
            phase = null;
        }
    }

    @Override
    protected void finalize() {
        System.out.println(this + " is finalized.");
    }

    public void nextPhase(GamePhase phase) {
        if (this.phase != null && phase != null) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] Pop phase out: " + this.phase);
            //endregion
            this.phase = phase;
            //region debug
            System.out.println("[" + server.getTickCounter() + "] Push phase in: " + phase);
            //endregion
        }
    }

    public void popPhaseIfPresent(GamePhase oldPhase) {
        if (phase == oldPhase) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] Pop phase out: " + phase);
            //endregion
            phase = null;
        }
    }
}
