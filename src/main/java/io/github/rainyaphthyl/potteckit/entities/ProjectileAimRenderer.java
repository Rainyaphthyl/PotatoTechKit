package io.github.rainyaphthyl.potteckit.entities;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.overlay.BaseOverlayRenderer;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.ModInfo;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.util.Reference;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class ProjectileAimRenderer extends BaseOverlayRenderer {
    private final Queue<AimRangePacket> aimPacketList = new LinkedList<>();
    private double distanceRate = 4.0;

    public ProjectileAimRenderer() {
    }

    public void addAimRange(AimRangePacket entries) {
        if (entries != null) {
            synchronized (aimPacketList) {
                needsUpdate = true;
                aimPacketList.clear();
                aimPacketList.add(entries);
            }
        }
    }

    public void removeAimRange() {
        synchronized (aimPacketList) {
            aimPacketList.clear();
        }
    }

    public synchronized double getDistanceRate() {
        return distanceRate;
    }

    public synchronized void resetDistanceRate() {
        distanceRate = 4.0;
    }

    @Override
    public void setNeedsUpdate() {
        super.setNeedsUpdate();
        synchronized (this) {
            aimPacketList.clear();
        }
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
        return Configs.projectileAimIndicator.getBooleanValue();
    }

    @Override
    public boolean needsUpdate(Entity entity, Minecraft mc) {
        return needsUpdate;
    }

    @Override
    public void update(Vec3d cameraPos, Entity entity, Minecraft mc) {
        for (AimRangePacket aimRangePacket : aimPacketList) {
            aimRangePacket.setCompleted();
            for (Double2ObjectMap.Entry<AimRangePacket.Range> mapEntry : aimRangePacket) {
                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double minZ = Double.MAX_VALUE;
                double maxX = -Double.MAX_VALUE;
                double maxY = -Double.MAX_VALUE;
                double maxZ = -Double.MAX_VALUE;
                double level = mapEntry.getDoubleKey();
                AimRangePacket.Range vertexList = mapEntry.getValue();
                for (AimRangePacket.Vertex vertex : vertexList) {
                    Vec3d pos = vertex.position;
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
                boolean single = vertexList.size() <= 1 || level == 0.0;
                int colorRGBAim;
                if (vertexList.isAtEntity()) {
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
