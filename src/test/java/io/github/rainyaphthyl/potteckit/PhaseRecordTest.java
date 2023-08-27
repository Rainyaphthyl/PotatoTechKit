package io.github.rainyaphthyl.potteckit;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.PhaseRecord;
import net.minecraft.world.DimensionType;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PhaseRecordTest {
    public static void main(String[] args) throws InterruptedException {
        Set<PhaseRecord> randomSet = Collections.synchronizedSet(new HashSet<>());
        Semaphore semaphore = new Semaphore(0);
        final int threadNum = 12;
        for (int i = 0; i < threadNum; ++i) {
            createStartThread(randomSet, semaphore);
        }
        semaphore.acquire(threadNum);
        SortedSet<PhaseRecord> sortedSet = new TreeSet<>(randomSet);
        {
            int i = 0;
            for (PhaseRecord record : sortedSet) {
                System.out.println("[" + i++ + "] " + record);
            }
        }
    }

    private static void createStartThread(Set<PhaseRecord> randomSet, Semaphore semaphore) {
        Thread thread = new Thread(() -> {
            for (GamePhase phase : GamePhase.values()) {
                if (phase.dimensional) {
                    for (DimensionType dimension : DimensionType.values()) {
                        randomSet.add(PhaseRecord.getPooledRecord(dimension, phase));
                    }
                } else {
                    randomSet.add(PhaseRecord.getPooledRecord(phase));
                }
            }
            semaphore.release();
        });
        thread.setDaemon(true);
        thread.start();
    }
}
