package io.github.rainyaphthyl.potteckit.client;

import io.github.rainyaphthyl.potteckit.config.Configs;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class RenderHelper {
    private static final Map<DimensionType, Long2LongMap> sectionBlacklists = Collections.synchronizedMap(new EnumMap<>(DimensionType.class));

    static {
        for (DimensionType dim : DimensionType.values()) {
            sectionBlacklists.putIfAbsent(dim, Long2LongMaps.synchronize(new Long2LongOpenHashMap()));
        }
    }

    public static boolean recheckLaggySection(RenderChunk renderSection) {
        if (renderSection == null) {
            return false;
        }
        boolean flag = false;
        World renderWorld = renderSection.getWorld();
        if (renderWorld instanceof WorldClient) {
            DimensionType dimensionType = renderWorld.provider.getDimensionType();
            Long2LongMap blacklist = sectionBlacklists.get(dimensionType);
            long blockIndex = renderSection.getPosition().toLong();
            if (blacklist.containsKey(blockIndex)) {
                long currTime = renderWorld.getTotalWorldTime();
                long prevTime = blacklist.get(blockIndex);
                if (currTime > prevTime) {
                    blacklist.remove(blockIndex);
                } else {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static void banLaggySection(RenderChunk renderSection, long duration) {
        if (Configs.chunkRebuildAutoBlacklist.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            // millis to nanos
            double threshold = Configs.chunkRebuildBlacklistThreshold.getIntegerValue() * 1.0e+6;
            if (duration > threshold) {
                World renderWorld = renderSection.getWorld();
                if (renderWorld instanceof WorldClient) {
                    DimensionType dimensionType = renderWorld.provider.getDimensionType();
                    Long2LongMap blacklist = sectionBlacklists.get(dimensionType);
                    long blockIndex = renderSection.getPosition().toLong();
                    long worldTime = renderWorld.getTotalWorldTime();
                    long banTime = (long) (duration * 900.0 / threshold);
                    blacklist.put(blockIndex, worldTime + banTime);
                }
            }
        }
    }
}
