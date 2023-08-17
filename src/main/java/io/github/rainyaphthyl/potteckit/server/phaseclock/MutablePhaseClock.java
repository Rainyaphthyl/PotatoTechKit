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
        if (!server.equals(that.server)) return false;
        return Objects.equals(toImmutable(), that.toImmutable());
    }

    @Override
    public int hashCode() {
        return server.hashCode();
    }

    public PhaseRecord toImmutable() {
        final DimensionType dimensionType;
        final GamePhase gamePhase;
        synchronized (this) {
            dimensionType = dimension;
            gamePhase = phase;
        }
        return PhaseRecord.getPooledRecord(dimensionType, gamePhase);
    }

    public synchronized boolean isDimensionValid() {
        if (phase == null) {
            return true;
        } else {
            boolean requiring = phase.dimensional;
            boolean actual = dimension != null;
            return requiring == actual;
        }
    }

    public void checkValidDimension() throws IllegalDimensionException {
        if (phase != null) {
            boolean requiring = phase.dimensional;
            boolean actual = dimension != null;
            if (requiring != actual) {
                throw new IllegalDimensionException(phase, requiring);
            }
        }
    }

    public synchronized DimensionType getDimension() {
        return dimension;
    }

    public synchronized void setDimension(DimensionType dimension) {
        this.dimension = dimension;
    }

    public synchronized GamePhase getPhase() {
        return phase;
    }

    public synchronized void pushPhase(GamePhase phase) throws IllegalDimensionException {
        if (this.phase == null && phase != null) {
            this.phase = phase;
            //region debug
            System.out.println("[" + server.getTickCounter() + "] (" + dimension + ") Push phase in: " + phase);
            //endregion
        }
        checkValidDimension();
    }

    public synchronized void popPhase() throws IllegalDimensionException {
        if (phase != null) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] (" + dimension + ") Pop phase out: " + phase);
            //endregion
            phase = null;
        }
        checkValidDimension();
    }

    public synchronized void nextPhase(GamePhase phase) throws IllegalDimensionException {
        if (this.phase != null && phase != null) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] (" + dimension + ") Pop phase out: " + this.phase);
            //endregion
            this.phase = phase;
            //region debug
            System.out.println("[" + server.getTickCounter() + "] (" + dimension + ") Push phase in: " + phase);
            //endregion
        }
        checkValidDimension();
    }

    public synchronized void popPhaseIfPresent(GamePhase oldPhase) throws IllegalDimensionException {
        if (phase == oldPhase) {
            //region debug
            System.out.println("[" + server.getTickCounter() + "] (" + dimension + ") Pop phase out: " + phase);
            //endregion
            phase = null;
        }
        checkValidDimension();
    }
}
