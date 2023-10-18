package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class EntityAimCamera extends Entity {
    private static Entity vanilla = null;
    private static boolean aiming = false;

    protected EntityAimCamera(WorldClient worldIn, double x, double y, double z, float yaw, float pitch) {
        super(worldIn);
        setLocationAndAngles(x, y, z, yaw, pitch);
    }

    public static void startAimSpectating(@Nonnull Vec3d pos, float yaw, float pitch) {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP playerSP = client.player;
        WorldClient worldClient = client.world;
        if (playerSP != null && worldClient != null) {
            Entity camera = client.getRenderViewEntity();
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            if (camera instanceof EntityAimCamera) {
                camera.setLocationAndAngles(x, y, z, yaw, pitch);
            } else {
                vanilla = camera;
                camera = new EntityAimCamera(worldClient, x, y, z, yaw, pitch);
                worldClient.spawnEntity(camera);
                client.setRenderViewEntity(camera);
            }
            aiming = true;
        }
    }

    public static void removeAimCamera() {
        if (aiming) {
            Minecraft client = Minecraft.getMinecraft();
            Entity camera = client.getRenderViewEntity();
            if (camera instanceof EntityAimCamera) {
                WorldClient worldClient = client.world;
                if (worldClient != null) {
                    worldClient.removeEntity(camera);
                }
            }
            client.setRenderViewEntity(vanilla);
            vanilla = null;
            aiming = false;
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
