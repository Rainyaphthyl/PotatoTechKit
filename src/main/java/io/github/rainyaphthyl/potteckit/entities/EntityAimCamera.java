package io.github.rainyaphthyl.potteckit.entities;

import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityAimCamera extends Entity {
    private static final AtomicBoolean aimingFlag = new AtomicBoolean(false);
    private static Entity vanillaCamera = null;
    private static boolean cullingFlag = false;
    private static float mouseSensitivity = -1.0F;
    protected final EntityPlayerSP shooter;

    protected EntityAimCamera(WorldClient worldIn, EntityPlayerSP shooter, double x, double y, double z, float yaw, float pitch) {
        super(worldIn);
        this.shooter = shooter;
        setLocationAndAngles(x, y, z, yaw, pitch);
    }

    public static synchronized void startAimSpectating(@Nonnull Vec3d pos, float yaw, float pitch) {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP playerSP = client.player;
        WorldClient worldClient = client.world;
        if (playerSP != null && worldClient != null) {
            Entity viewEntity = client.getRenderViewEntity();
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            if (Configs.projectileAccurateAim.getBooleanValue()) {
                GameSettings gameSettings = client.gameSettings;
                if (gameSettings != null) {
                    if (mouseSensitivity < 0.0F) {
                        mouseSensitivity = gameSettings.mouseSensitivity;
                    }
                    double rate = Renderers.PROJECTILE_AIM_RENDERER.getDistanceRate() / 4.0;
                    float fovZoom = Configs.projectileAccurateAim.getFloatValue() / 200.0F;
                    gameSettings.mouseSensitivity = fovZoom * (float) rate;
                }
            }
            if (viewEntity instanceof EntityAimCamera) {
                EntityAimCamera camera = (EntityAimCamera) viewEntity;
                if (camera.posX != x || camera.posY != y || camera.posZ != z || camera.rotationYaw != yaw || camera.rotationPitch != pitch) {
                    camera.setLocationAndAngles(x, y, z, yaw, pitch);
                }
            } else {
                vanillaCamera = viewEntity;
                cullingFlag = client.renderChunksMany;
                EntityAimCamera camera = new EntityAimCamera(worldClient, playerSP, x, y, z, yaw, pitch);
                worldClient.spawnEntity(camera);
                client.setRenderViewEntity(camera);
                client.renderChunksMany = false;
            }
            aimingFlag.set(true);
        }
    }

    public static synchronized void removeAimCamera() {
        if (aimingFlag.get()) {
            Minecraft client = Minecraft.getMinecraft();
            Entity camera = client.getRenderViewEntity();
            if (camera instanceof EntityAimCamera) {
                WorldClient worldClient = client.world;
                if (worldClient != null) {
                    worldClient.removeEntity(camera);
                }
            }
            client.setRenderViewEntity(vanillaCamera);
            client.renderChunksMany = cullingFlag;
            if (Configs.projectileAccurateAim.getBooleanValue()) {
                GameSettings gameSettings = client.gameSettings;
                if (gameSettings != null && mouseSensitivity >= 0.0F) {
                    gameSettings.mouseSensitivity = mouseSensitivity;
                    mouseSensitivity = -1.0F;
                }
            }
            vanillaCamera = null;
            cullingFlag = false;
            aimingFlag.set(false);
            Renderers.PROJECTILE_AIM_RENDERER.resetDistanceRate();
        }
    }

    public boolean isCurrentShooter(EntityPlayer player) {
        return player == shooter;
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
