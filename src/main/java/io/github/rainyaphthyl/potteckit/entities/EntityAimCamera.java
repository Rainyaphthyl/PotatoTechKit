package io.github.rainyaphthyl.potteckit.entities;

import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityAimCamera extends Entity {
    private static final AtomicBoolean aimingFlag = new AtomicBoolean(false);
    private static Entity vanillaCamera = null;
    private static boolean cullingFlag = false;

    protected EntityAimCamera(WorldClient worldIn, double x, double y, double z, float yaw, float pitch) {
        super(worldIn);
        setLocationAndAngles(x, y, z, yaw, pitch);
    }

    public static synchronized void startAimSpectating(@Nonnull Vec3d pos, float yaw, float pitch) {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP playerSP = client.player;
        WorldClient worldClient = client.world;
        if (playerSP != null && worldClient != null) {
            Entity camera = client.getRenderViewEntity();
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            if (camera instanceof EntityAimCamera) {
                if (camera.posX != x || camera.posY != y || camera.posZ != z || camera.rotationYaw != yaw || camera.rotationPitch != pitch) {
                    MessageOutput.CHAT.send(String.format("Camera loc & rot: %.2f, %.2f, %.2f, %.2f, %.2f", x, y, z, yaw, pitch), MessageDispatcher.generic());
                }
                camera.setLocationAndAngles(x, y, z, yaw, pitch);
            } else {
                vanillaCamera = camera;
                cullingFlag = client.renderChunksMany;
                camera = new EntityAimCamera(worldClient, x, y, z, yaw, pitch);
                worldClient.spawnEntity(camera);
                client.setRenderViewEntity(camera);
                client.renderChunksMany = false;
                MessageOutput.CHAT.send(String.format("Start aiming: %.2f, %.2f, %.2f, %.2f, %.2f", x, y, z, yaw, pitch), MessageDispatcher.generic());
            }
            aimingFlag.set(true);
        }
    }

    public static synchronized void removeAimCamera() {
        if (aimingFlag.get()) {
            Minecraft client = Minecraft.getMinecraft();
            Entity camera = client.getRenderViewEntity();
            MessageOutput.CHAT.send("Stop aiming!", MessageDispatcher.generic());
            if (camera instanceof EntityAimCamera) {
                WorldClient worldClient = client.world;
                if (worldClient != null) {
                    worldClient.removeEntity(camera);
                }
            }
            client.setRenderViewEntity(vanillaCamera);
            client.renderChunksMany = cullingFlag;
            vanillaCamera = null;
            cullingFlag = false;
            aimingFlag.set(false);
        }
    }

    @Override
    protected void entityInit() {
        setSize(0.0F, 0.0F);
        setEntityInvulnerable(true);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void spawnRunningParticles() {
    }

    @Override
    public boolean handleWaterMovement() {
        return inWater;
    }

    @Override
    public void applyEntityCollision(@Nonnull Entity entityIn) {
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
    }
}
