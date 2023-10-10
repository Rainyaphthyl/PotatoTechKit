package io.github.rainyaphthyl.potteckit.chunkphase.phaseclock;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.SubPhase;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutablePhaseClock {
    public static final int ENABLED = 0b1;
    public static final int DETAILED = 0b10;
    private static final ConcurrentMap<MinecraftServer, MutablePhaseClock> serverClockPool = new ConcurrentHashMap<>();
    private final Lock writeLock;
    private final Lock readLock;
    private final MinecraftServer server;
    private final AtomicInteger statusFlags = new AtomicInteger(0);
    private final Map<GamePhase, SubPhaseClock> subClockMap = Collections.synchronizedMap(new EnumMap<>(GamePhase.class));
    private DimensionType dimension = null;
    private GamePhase phase = null;
    private SubPhaseClock subClock = null;
    /**
     * This only increases when an event is marked.
     */
    private int eventOrdinal = 0;
    private boolean running = false;
    private boolean detailMode = false;

    private MutablePhaseClock(MinecraftServer server) {
        this.server = Objects.requireNonNull(server);
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        writeLock = lock.writeLock();
        readLock = lock.readLock();
        for (GamePhase gamePhase : GamePhase.values()) {
            SubPhaseClock clock = gamePhase.createSubPhaseClock(this);
            if (clock != null) {
                subClockMap.put(gamePhase, clock);
            }
        }
        syncFromConfigs();
        updateStatus();
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

    public void start() {
        statusFlags.updateAndGet(operand -> operand | ENABLED);
    }

    public void stop() {
        statusFlags.updateAndGet(operand -> operand & ~ENABLED);
    }

    public void setRunning(boolean flag) {
        if (flag) {
            start();
        } else {
            stop();
        }
    }

    public void setDetailMode(boolean flag) {
        if (flag) {
            startDetailMode();
        } else {
            stopDetailMode();
        }
    }

    public void startDetailMode() {
        statusFlags.updateAndGet(operand -> operand | DETAILED);
    }

    public void stopDetailMode() {
        statusFlags.updateAndGet(operand -> operand & ~DETAILED);
    }

    public void syncFromConfigs() {
        boolean shouldRun = Configs.chunkLoadingGraph.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        setRunning(shouldRun);
        boolean requiresDetail = Configs.chunkLoadingDetails.getBooleanValue() && Configs.enablePotteckit.getBooleanValue();
        setDetailMode(requiresDetail);
    }

    public void updateStatus() {
        boolean wasRunning, wasDetailed;
        try {
            readLock.lock();
            wasRunning = running;
            wasDetailed = detailMode;
        } finally {
            readLock.unlock();
        }
        int flags = statusFlags.get();
        boolean isRunning = (flags & ENABLED) != 0;
        if (wasRunning != isRunning) {
            running = isRunning;
            dimension = null;
            phase = null;
        }
        boolean isDetailed = (flags & DETAILED) != 0;
        if (wasDetailed != isDetailed) {
            detailMode = isDetailed;
            subClock = null;
            eventOrdinal = 0;
        }
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

    public void checkValidDimension() {
        try {
            readLock.lock();
            if (phase != null) {
                boolean requiring = phase.dimensional;
                boolean actual = dimension != null;
                if (requiring != actual) {
                    Reference.LOGGER.error(phase + " should" + (requiring ? " " : " NOT ") + "be dimensional.");
                    stop();
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
        if (running) {
            try {
                writeLock.lock();
                this.dimension = dimension;
            } finally {
                writeLock.unlock();
            }
        }
    }

    public void startNextDimension() {
        if (running) {
            try {
                writeLock.lock();
                if (dimension == null) {
                    dimension = DimensionType.OVERWORLD;
                } else {
                    switch (dimension) {
                        case OVERWORLD:
                            dimension = DimensionType.NETHER;
                            break;
                        case NETHER:
                            dimension = DimensionType.THE_END;
                            break;
                        case THE_END:
                        default:
                            dimension = null;
                    }
                }
            } finally {
                writeLock.unlock();
            }
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

    /**
     * Specific operation to each sub-phase
     */
    public void operateSubPhase(GamePhase gamePhase, Object... args) {
        if (subClock != null && gamePhase != null && args != null) {
            try {
                writeLock.lock();
                if (gamePhase == phase && subClock.operate(args)) {
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    public void pushSubPhase() {
        if (subClock != null) {
            try {
                writeLock.lock();
                if (subClock.push()) {
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    public void swapSubPhase() {
        if (subClock != null) {
            try {
                writeLock.lock();
                if (subClock.swap()) {
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    public void popSubPhase() {
        if (subClock != null) {
            try {
                writeLock.lock();
                if (subClock.pop()) {
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Nullable
    public TickRecord markCurrentTickStamp() {
        try {
            readLock.lock();
            int tickOrdinal = server.getTickCounter();
            int dim = dimension == null ? 0 : dimension.getId();
            long gameTime = server.getWorld(dim).getTotalWorldTime();
            SubPhase subRecord = subClock == null ? null : subClock.createRecord();
            TickRecord tickRecord = TickRecord.getInstance(tickOrdinal, gameTime, dimension, phase, subRecord, eventOrdinal);
            if (tickRecord != null) {
                ++eventOrdinal;
            }
            return tickRecord;
        } finally {
            readLock.unlock();
        }
    }

    @Nullable
    public TickRecord markCurrentTickStamp(boolean mainThreadOnly) {
        if (mainThreadOnly && !server.isCallingFromMinecraftThread()) {
            return null;
        } else {
            return markCurrentTickStamp();
        }
    }

    public void pushPhase(GamePhase phase) {
        if (running) {
            try {
                writeLock.lock();
                if (this.phase == null && phase != null) {
                    this.phase = phase;
                    if (detailMode) {
                        subClock = subClockMap.get(phase);
                        if (subClock != null) {
                            subClock.reset();
                        }
                    }
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
            checkValidDimension();
        }
    }

    public void popPhase() {
        if (running) {
            try {
                writeLock.lock();
                if (phase != null) {
                    phase = null;
                    subClock = null;
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
            checkValidDimension();
        }
    }

    public void swapPhase(GamePhase phase) {
        if (running) {
            try {
                writeLock.lock();
                if (this.phase != null && phase != null) {
                    this.phase = phase;
                    if (detailMode) {
                        subClock = subClockMap.get(phase);
                        if (subClock != null) {
                            subClock.reset();
                        }
                    }
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
            checkValidDimension();
        }
    }

    public void popPhaseIfPresent(GamePhase oldPhase) {
        if (running) {
            try {
                writeLock.lock();
                if (phase == oldPhase) {
                    phase = null;
                    subClock = null;
                    eventOrdinal = 0;
                }
            } finally {
                writeLock.unlock();
            }
            checkValidDimension();
        }
    }

    public abstract static class SubPhaseClock {
        protected final MutablePhaseClock parentClock;
        protected final Lock writeLock;
        protected final Lock readLock;
        protected final MinecraftServer server;

        public SubPhaseClock(MutablePhaseClock parentClock) {
            this.parentClock = Objects.requireNonNull(parentClock);
            writeLock = parentClock.writeLock;
            readLock = parentClock.readLock;
            server = parentClock.server;
            reset();
        }

        /**
         * This method does not increase the event ordinal.
         */
        public abstract SubPhase createRecord();

        /**
         * This is called in {@code super()}
         */
        public abstract void reset();

        protected abstract boolean push();

        protected abstract boolean swap();

        protected abstract boolean pop();

        /**
         * @return {@code true} if the event ordinal need to be increased later
         */
        protected abstract boolean operate(@Nonnull Object... args);
    }
}
