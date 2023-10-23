package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class ArrowSimulator {
    public static final float DEG_TO_RAD = 0.017453292F;
    public static final int MAX_TICK_COUNT = 1200;
    protected static final double RAD_TO_DEG = 180.0 / Math.PI;
    protected static final Predicate<Entity> ARROW_TARGETS = EntitySelectors.NOT_SPECTATING.and(EntitySelectors.IS_ALIVE).and(Entity::canBeCollidedWith);
    protected static final double GAUSSIAN_SCALE = 0.007499999832361937D;
    protected static final float height = 0.5F;
    protected static final float width = 0.5F;
    /**
     * The owner of this arrow.
     */
    public final Entity shooter;
    public final WorldClient world;
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
    protected boolean inWater = false;
    protected boolean firstUpdate;
    private int xTile;
    private int yTile;
    private int zTile;
    private int ticksInAir;
    private boolean stopped = false;
    private Vec3d hitPoint = null;
    private Vec3d hitMotion = null;

    public ArrowSimulator(Entity shooter, WorldClient world) {
        this.shooter = shooter;
        this.world = world;
    }

    public void predictDestination(float velocity, float inaccuracy) {
        float pitch = shooter.rotationPitch;
        float yaw = shooter.rotationYaw;
        float yawDegree = yaw * DEG_TO_RAD;
        float pitchDegree = pitch * DEG_TO_RAD;
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
                                float cameraYaw = -(float) (MathHelper.atan2(hitMotion.x, hitMotion.z) * RAD_TO_DEG);
                                float cameraPitch = -(float) (MathHelper.atan2(hitMotion.y, length) * RAD_TO_DEG);
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
            mx += dirX * sigmaLevel * GAUSSIAN_SCALE * inaccuracy;
            my += dirY * sigmaLevel * GAUSSIAN_SCALE * inaccuracy;
            mz += dirZ * sigmaLevel * GAUSSIAN_SCALE * inaccuracy;
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
        boolean hitEntity = false;
        for (int i = 0; !(stopped || inGround) && i < MAX_TICK_COUNT; ++i) {
            hitEntity |= onUpdate();
        }
        return hitEntity;
    }

    /**
     * {@link EntityArrow#onUpdate()}
     */
    public boolean onUpdate() {
        boolean hitEntity = false;
        onEntityUpdate();
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            double f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
            rotationPitch = (float) (MathHelper.atan2(motionY, f) * (180D / Math.PI));
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
        }
        BlockPos blockpos = new BlockPos(xTile, yTile, zTile);
        IBlockState iblockstate = world.getBlockState(blockpos);
        if (iblockstate.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(world, blockpos);
            if (axisalignedbb != null && axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(posX, posY, posZ))) {
                inGround = true;
            }
        }
        hitMotion = new Vec3d(motionX, motionY, motionZ);
        if (this.inGround) {
            this.setDead();
        } else {
            ++ticksInAir;
            Vec3d currPos = new Vec3d(posX, posY, posZ);
            Vec3d nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult raytraceresult = world.rayTraceBlocks(currPos, nextPos, false, true, false);
            currPos = new Vec3d(posX, posY, posZ);
            nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (raytraceresult != null) {
                hitPoint = raytraceresult.hitVec;
                nextPos = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }
            Entity entity = findEntityOnPath(currPos, nextPos);
            if (entity != null && !entity.getUniqueID().equals(shooter.getUniqueID())) {
                raytraceresult = new RayTraceResult(entity);
            }
            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) raytraceresult.entityHit;
                if (shooter instanceof EntityPlayer && !((EntityPlayer) shooter).canAttackPlayer(entityplayer)) {
                    raytraceresult = null;
                }
            }
            if (raytraceresult != null) {
                onHit(raytraceresult);
                hitEntity = raytraceresult.entityHit != null;
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            double f4 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
            rotationPitch = (float) (MathHelper.atan2(motionY, f4) * (180D / Math.PI));
            while (rotationPitch - prevRotationPitch < -180.0F) {
                prevRotationPitch -= 360.0F;
            }
            while (rotationPitch - prevRotationPitch >= 180.0F) {
                prevRotationPitch += 360.0F;
            }
            while (rotationYaw - prevRotationYaw < -180.0F) {
                prevRotationYaw -= 360.0F;
            }
            while (rotationYaw - prevRotationYaw >= 180.0F) {
                prevRotationYaw += 360.0F;
            }
            rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
            rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
            float friction = 0.99F;
            if (inWater) {
                friction = 0.6F;
            }
            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;
            motionY -= 0.05000000074505806D;
            setPosition(posX, posY, posZ);
        }
        return hitEntity;
    }

    public Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity target = null;
        List<Entity> list = world.getEntitiesInAABBexcluding(null, boundingBox.expand(motionX, motionY, motionZ).grow(1.0), ARROW_TARGETS::test);
        double minDistSq = 0.0;
        for (Entity tested : list) {
            if (tested != shooter || ticksInAir >= 5) {
                AxisAlignedBB axisAlignedBB = tested.getEntityBoundingBox().grow(0.30000001192092896);
                RayTraceResult rayTraceResult = axisAlignedBB.calculateIntercept(start, end);
                if (rayTraceResult != null) {
                    double tempDistSq = start.squareDistanceTo(rayTraceResult.hitVec);
                    if (tempDistSq < minDistSq || minDistSq == 0.0) {
                        target = tested;
                        minDistSq = tempDistSq;
                        hitPoint = rayTraceResult.hitVec;
                    }
                }
            }
        }
        return target;
    }

    public void onEntityUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;
        firstUpdate = false;
    }

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
        double radius = width / 2.0F;
        boundingBox = new AxisAlignedBB(x - radius, y, z - radius, x + radius, y + (double) height, z + radius);
    }

    protected void onHit(@Nonnull RayTraceResult raytraceResultIn) {
        Entity entity = raytraceResultIn.entityHit;
        if (entity == null) {
            BlockPos blockpos = raytraceResultIn.getBlockPos();
            xTile = blockpos.getX();
            yTile = blockpos.getY();
            zTile = blockpos.getZ();
            motionX = (float) (raytraceResultIn.hitVec.x - posX);
            motionY = (float) (raytraceResultIn.hitVec.y - posY);
            motionZ = (float) (raytraceResultIn.hitVec.z - posZ);
            hitPoint = raytraceResultIn.hitVec;
            float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            posX -= motionX / (double) f2 * 0.05000000074505806D;
            posY -= motionY / (double) f2 * 0.05000000074505806D;
            posZ -= motionZ / (double) f2 * 0.05000000074505806D;
            inGround = true;
        }
        setDead();
    }

    public void setDead() {
        stopped = true;
    }
}