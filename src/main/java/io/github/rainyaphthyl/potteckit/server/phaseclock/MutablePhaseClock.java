package io.github.rainyaphthyl.potteckit.server.phaseclock;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutablePhaseClock {
    private static final ConcurrentMap<MinecraftServer, MutablePhaseClock> serverClockPool = new ConcurrentHashMap<>();
    private final Lock writeLock;
    private final Lock readLock;
    private final MinecraftServer server;
    private DimensionType dimension = null;
    private GamePhase phase = null;

    {
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

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
        return Objects.equals(getRecord(), that.getRecord());
    }

    @Override
    public int hashCode() {
        return server.hashCode();
    }

    public PhaseRecord getRecord() {
        final DimensionType dimensionType;
        final GamePhase gamePhase;
        try {
            readLock.lock();
            dimensionType = dimension;
            gamePhase = phase;
        } finally {
            readLock.unlock();
        }
        return PhaseRecord.getPooledRecord(dimensionType, gamePhase);
    }

    public boolean isDimensionValid() {
        try {
            readLock.lock();
            if (phase == null) {
                return true;
            } else {
                boolean requiring = phase.dimensional;
                boolean actual = dimension != null;
                return requiring == actual;
            }
        } finally {
            readLock.unlock();
        }
    }

    public void checkValidDimension() throws IllegalDimensionException {
        try {
            readLock.lock();
            if (phase != null) {
                boolean requiring = phase.dimensional;
                boolean actual = dimension != null;
                if (requiring != actual) {
                    throw new IllegalDimensionException(phase, requiring);
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    public DimensionType getDimension() {
        try {
            readLock.lock();
            return dimension;
        } finally {
            readLock.unlock();
        }
    }

    public void setDimension(DimensionType dimension) {
        try {
            writeLock.lock();
            this.dimension = dimension;
        } finally {
            writeLock.unlock();
        }
    }

    public GamePhase getPhase() {
        try {
            readLock.lock();
            return phase;
        } finally {
            readLock.unlock();
        }
    }

    public void pushPhase(GamePhase phase) throws IllegalDimensionException {
        try {
            writeLock.lock();
            if (this.phase == null && phase != null) {
                this.phase = phase;
            }
        } finally {
            writeLock.unlock();
        }
        checkValidDimension();
    }

    public void popPhase() throws IllegalDimensionException {
        try {
            writeLock.lock();
            if (phase != null) {
                phase = null;
            }
        } finally {
            writeLock.unlock();
        }
        checkValidDimension();
    }

    public void swapPhase(GamePhase phase) throws IllegalDimensionException {
        try {
            writeLock.lock();
            if (this.phase != null && phase != null) {
                this.phase = phase;
            }
        } finally {
            writeLock.unlock();
        }
        checkValidDimension();
    }

    public void popPhaseIfPresent(GamePhase oldPhase) throws IllegalDimensionException {
        try {
            writeLock.lock();
            if (phase == oldPhase) {
                phase = null;
            }
        } finally {
            writeLock.unlock();
        }
        checkValidDimension();
    }
}
