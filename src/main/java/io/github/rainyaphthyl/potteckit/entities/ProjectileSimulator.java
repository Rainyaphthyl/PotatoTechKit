package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public abstract class ProjectileSimulator {
    public static final float DEG_TO_RAD = 0.017453292F;
    public static final int MAX_TICK_COUNT = 1200;
    protected static final double RAD_TO_DEG = 180.0 / Math.PI;
    protected static final Predicate<Entity> ARROW_TARGETS = EntitySelectors.NOT_SPECTATING.and(EntitySelectors.IS_ALIVE).and(Entity::canBeCollidedWith);
    protected static final double GAUSSIAN_SCALE = 0.007499999832361937D;
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
    protected boolean stopped = false;
    protected boolean firstUpdate;
    protected int ticksInAir;
    protected int ticksExisted;
    protected boolean inWater = false;
    protected int xTile;
    protected int yTile;
    protected int zTile;

    protected ProjectileSimulator(Entity shooter, WorldClient world, float width, float height) {
        this.shooter = shooter;
        this.world = world;
        this.width = width;
        this.height = height;
    }

    public void predictDestination(float pitchOffset, float velocity, float inaccuracy) {
        float pitch = shooter.rotationPitch;
        float yaw = shooter.rotationYaw;
        float cosPitch = MathHelper.cos(pitch * ProjectileSimulator.DEG_TO_RAD);
        float yawDegree = yaw * ProjectileSimulator.DEG_TO_RAD;
        float rx = -MathHelper.sin(yawDegree) * cosPitch;
        float ry = -MathHelper.sin((pitch + pitchOffset) * ProjectileSimulator.DEG_TO_RAD);
        float rz = MathHelper.cos(yawDegree) * cosPitch;
        AimRangePacket rangePacket = new AimRangePacket(width, height);
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
                                float cameraYaw = -(float) (MathHelper.atan2(hitMotion.x, hitMotion.z) * ProjectileSimulator.RAD_TO_DEG);
                                float cameraPitch = -(float) (MathHelper.atan2(hitMotion.y, length) * ProjectileSimulator.RAD_TO_DEG);
                                Vec3d hitCenter = hitPoint.add(0.0, height / 2, 0.0);
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
            mx += dirX * sigmaLevel * ProjectileSimulator.GAUSSIAN_SCALE * inaccuracy;
            my += dirY * sigmaLevel * ProjectileSimulator.GAUSSIAN_SCALE * inaccuracy;
            mz += dirZ * sigmaLevel * ProjectileSimulator.GAUSSIAN_SCALE * inaccuracy;
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

    public boolean simulateMovement() {
        stopped = false;
        inGround = false;
        hitPoint = null;
        hitMotion = null;
        ticksExisted = 0;
        boolean hitEntity = false;
        for (int i = 0; !(stopped || inGround) && i < ProjectileSimulator.MAX_TICK_COUNT; ++i) {
            ++ticksExisted;
            hitEntity |= onUpdate();
        }
        return hitEntity;
    }

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
        double radius = width / 2.0F;
        boundingBox = new AxisAlignedBB(x - radius, y, z - radius, x + radius, y + (double) height, z + radius);
    }

    public void setDead() {
        stopped = true;
    }

    public void onEntityUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;
        firstUpdate = false;
    }

    public abstract boolean onUpdate();

    public abstract Entity findEntityOnPath(Vec3d start, Vec3d end);
}
