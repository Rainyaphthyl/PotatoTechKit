package io.github.rainyaphthyl.potteckit;

import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.BlockEventSubPhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.subphase.SubPhase;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SubPhaseBlockEventTest {
    public static void main(String[] args) {
        Set<SubPhase> subPhaseSet = new HashSet<>();
        for (int i = 0; i < 2; ++i) {
            for (int depth = 0; depth < 4; ++depth) {
                for (int order = 0; order < 5; ++order) {
                    SubPhase subPhase = new BlockEventSubPhase(depth, order);
                    if (!subPhaseSet.add(subPhase)) {
                        System.out.println("Repeating: " + subPhase);
                    }
                }
            }
        }
        SortedSet<SubPhase> sortedSet = new TreeSet<>(subPhaseSet);
        System.out.println("=============================");
        for (SubPhase subPhase : subPhaseSet) {
            System.out.println(subPhase);
        }
        System.out.println("=============================");
        for (SubPhase subPhase : sortedSet) {
            System.out.println(subPhase);
        }
    }
}
