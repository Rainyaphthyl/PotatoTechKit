package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class ProjectileSimulator {
    /**
     * The owner of this arrow.
     */
    public final Entity shooter;
    public final WorldClient world;
    protected final float width;
    protected final float height;
    protected boolean inGround;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double prevPosX;
    protected double prevPosY;
    protected double prevPosZ;
    protected double motionX;
    protected double motionY;
    protected double motionZ;
    protected float rotationYaw;
    protected float rotationPitch;
    protected float prevRotationPitch;
    protected float prevRotationYaw;
    protected AxisAlignedBB boundingBox;
    protected Vec3d hitPoint = null;
    protected Vec3d hitMotion = null;

    protected ProjectileSimulator(Entity shooter, WorldClient world, float width, float height) {
        this.shooter = shooter;
        this.world = world;
        this.width = width;
        this.height = height;
    }

    public void predictDestination(float velocity, float inaccuracy) {
        float pitch = shooter.rotationPitch;
        float yaw = shooter.rotationYaw;
        float yawDegree = yaw * ArrowSimulator.DEG_TO_RAD;
        float pitchDegree = pitch * ArrowSimulator.DEG_TO_RAD;
        float cosPitch = MathHelper.cos(pitchDegree);
        float rx = -MathHelper.sin(yawDegree) * cosPitch;
        float ry = -MathHelper.sin(pitchDegree);
        float rz = MathHelper.cos(yawDegree) * cosPitch;
        AimRangePacket rangePacket = new AimRangePacket();
        for (int range = 0; range <= 3; ++range) {
            boolean notEnough = true;
            for (byte dirX = -1; notEnough && dirX <= 1; ++dirX) {
                for (byte dirY = -1; notEnough && dirY <= 1; ++dirY) {
                    for (byte dirZ = -1; notEnough && dirZ <= 1; ++dirZ) {
                        setPosition(shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
                        boolean valid = true;
                        if (range == 0) {
                            dirX = 0;
                            dirY = 0;
                            dirZ = 0;
                            notEnough = false;
                        } else if (dirX == 0 && dirY == 0 && dirZ == 0) {
                            valid = false;
                        }
                        if (valid) {
                            setInitMotion(rx, ry, rz, velocity, inaccuracy, range, dirX, dirY, dirZ);
                            motionX += shooter.motionX;
                            motionZ += shooter.motionZ;
                            if (!shooter.onGround) {
                                motionY += shooter.motionY;
                            }
                            boolean hitEntity = simulateMovement();
                            if (hitPoint != null) {
                                rangePacket.addVertexAtLevel(range, hitPoint, hitEntity);
                            }
                            if (range == 0 && hitMotion != null && hitPoint != null) {
                                double length = MathHelper.sqrt(hitMotion.x * hitMotion.x + hitMotion.z * hitMotion.z);
                                float cameraYaw = -(float) (MathHelper.atan2(hitMotion.x, hitMotion.z) * ArrowSimulator.RAD_TO_DEG);
                                float cameraPitch = -(float) (MathHelper.atan2(hitMotion.y, length) * ArrowSimulator.RAD_TO_DEG);
                                Vec3d hitCenter = hitPoint.add(0.0, 0.25, 0.0);
                                double rate = Renderers.PROJECTILE_AIM_RENDERER.getDistanceRate();
                                Vec3d posToTarget = hitMotion.scale(-rate).add(hitCenter);
                                EntityAimCamera.startAimSpectating(posToTarget, cameraYaw, cameraPitch);
                            }
                        }
                    }
                }
            }
        }
        rangePacket.setCompleted();
        Renderers.PROJECTILE_AIM_RENDERER.addAimRange(rangePacket);
    }

    public void setInitMotion(double mx, double my, double mz, float velocity, double inaccuracy, double sigmaLevel, byte dirX, byte dirY, byte dirZ) {
        double scale = MathHelper.sqrt(mx * mx + my * my + mz * mz);
        mx /= scale;
        my /= scale;
        mz /= scale;
        if (sigmaLevel != 0) {
            mx += dirX * sigmaLevel * ArrowSimulator.GAUSSIAN_SCALE * inaccuracy;
            my += dirY * sigmaLevel * ArrowSimulator.GAUSSIAN_SCALE * inaccuracy;
            mz += dirZ * sigmaLevel * ArrowSimulator.GAUSSIAN_SCALE * inaccuracy;
        }
        mx *= velocity;
        my *= velocity;
        mz *= velocity;
        motionX = mx;
        motionY = my;
        motionZ = mz;
        double horizonScale = MathHelper.sqrt(mx * mx + mz * mz);
        rotationYaw = (float) (MathHelper.atan2(mx, mz) * (180.0 / Math.PI));
        rotationPitch = (float) (MathHelper.atan2(my, horizonScale) * (180.0 / Math.PI));
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    public abstract boolean simulateMovement();

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
        double radius = width / 2.0F;
        boundingBox = new AxisAlignedBB(x - radius, y, z - radius, x + radius, y + (double) height, z + radius);
    }
}
