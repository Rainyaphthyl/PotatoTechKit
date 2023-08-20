package io.github.rainyaphthyl.potteckit.client;

import it.unimi.dsi.fastutil.objects.*;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderGlobals {
    public static final Object2ObjectMap<Thread, Semaphore> semaphores = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    public static final Object2IntMap<Thread> sectionNum = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
    public static final AtomicBoolean asyncImmediate = new AtomicBoolean(false);
}
