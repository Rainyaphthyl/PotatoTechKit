package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class EntityAimCamera extends Entity {
    private static EntityAimCamera camera = null;
    private static Entity vanilla = null;
    private static boolean flagRenderMany = false;

    protected EntityAimCamera(WorldClient worldIn, double x, double y, double z, float yaw, float pitch) {
        super(worldIn);
        setLocationAndAngles(x, y, z, yaw, pitch);
    }

    public static void startAimSpectating(@Nonnull Vec3d pos, float yaw, float pitch) {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP playerSP = client.player;
        WorldClient worldClient = client.world;
        if (playerSP != null && worldClient != null) {
            vanilla = client.getRenderViewEntity();
            flagRenderMany = client.renderChunksMany;
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            camera = new EntityAimCamera(worldClient, x, y, z, yaw, pitch);
            worldClient.spawnEntity(camera);
            client.setRenderViewEntity(camera);
            client.renderChunksMany = false;
        }
    }

    public static void removeAimCamera() {
        if (camera != null) {
            Minecraft client = Minecraft.getMinecraft();
            client.setRenderViewEntity(vanilla);
            client.renderChunksMany = flagRenderMany;
            WorldClient worldClient = client.world;
            if (worldClient != null) {
                worldClient.removeEntity(camera);
            }
            vanilla = null;
            flagRenderMany = false;
            camera = null;
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
