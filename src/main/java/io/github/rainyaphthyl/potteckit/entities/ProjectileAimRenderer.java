package io.github.rainyaphthyl.potteckit.entities;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.overlay.BaseOverlayRenderer;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.ModInfo;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.util.Reference;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class ProjectileAimRenderer extends BaseOverlayRenderer {
    public static final double MAX_DISTANCE = 128.0;
    public final Int2ObjectSortedMap<List<Vec3d>> aimListMap = new Int2ObjectAVLTreeMap<>();
    public final Int2BooleanMap aimDamageMap = new Int2BooleanOpenHashMap();
    private double distanceRate = 4.0;

    public ProjectileAimRenderer() {
    }

    public synchronized double getDistanceRate() {
        return distanceRate;
    }

    public synchronized void operateDistanceRate(@Nonnull DoubleUnaryOperator updater) {
        distanceRate = updater.applyAsDouble(distanceRate);
    }

    public synchronized void resetDistanceRate() {
        distanceRate = 4.0;
    }

    @Override
    public ModInfo getModInfo() {
        return Reference.MOD_INFO;
    }

    @Nullable
    @Override
    public Path getSaveFile(boolean isDimensionChangeOnly) {
        return null;
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return mc != null;
    }

    @Override
    public boolean needsUpdate(Entity entity, Minecraft mc) {
        return mc != null;
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, Minecraft mc) {
        for (Int2ObjectMap.Entry<List<Vec3d>> mapEntry : aimListMap.int2ObjectEntrySet()) {
            List<Vec3d> list = mapEntry.getValue();
            if (list != null && !list.isEmpty()) {
                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double minZ = Double.MAX_VALUE;
                double maxX = -Double.MAX_VALUE;
                double maxY = -Double.MAX_VALUE;
                double maxZ = -Double.MAX_VALUE;
                int level = mapEntry.getIntKey();
                for (Vec3d pos : list) {
                    if (pos.x < minX) {
                        minX = pos.x;
                    }
                    if (pos.y < minY) {
                        minY = pos.y;
                    }
                    if (pos.z < minZ) {
                        minZ = pos.z;
                    }
                    if (pos.x > maxX) {
                        maxX = pos.x;
                    }
                    if (pos.y > maxY) {
                        maxY = pos.y;
                    }
                    if (pos.z > maxZ) {
                        maxZ = pos.z;
                    }
                }
                boolean single = list.size() <= 1;
                int colorRGBAim;
                if (aimDamageMap.get(level)) {
                    colorRGBAim = single ? Configs.projectileCenterColor.getFirstColorInt() : Configs.projectileRangeColor.getFirstColorInt();
                } else {
                    colorRGBAim = single ? Configs.projectileCenterColor.getSecondColorInt() : Configs.projectileRangeColor.getSecondColorInt();
                }
                minX -= 0.25;
                minZ -= 0.25;
                maxX += 0.25;
                maxY += 0.5;
                maxZ += 0.25;
                BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR, false);
                ShapeRenderUtils.renderBoxEdgeLines(
                        minX - cameraPos.x, minY - cameraPos.y, minZ - cameraPos.z,
                        maxX - cameraPos.x, maxY - cameraPos.y, maxZ - cameraPos.z,
                        Color4f.fromColor(colorRGBAim), buffer
                );
                RenderUtils.drawBuffer();
            }
        }
    }
}
