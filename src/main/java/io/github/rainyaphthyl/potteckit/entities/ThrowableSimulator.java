package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ThrowableSimulator extends ProjectileSimulator {
    private final Item item;

    public ThrowableSimulator(Entity shooter, WorldClient world, Item item) {
        super(shooter, world, 0.25F, 0.25F);
        this.item = item;
    }

    @Override
    public boolean onUpdate() {
        boolean hitEntity = false;
        onEntityUpdate();
        hitMotion = new Vec3d(motionX, motionY, motionZ);
        if (inGround) {
            setDead();
        } else {
            ++ticksInAir;
            Vec3d currPos = new Vec3d(posX, posY, posZ);
            Vec3d nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult rayTraceResult = world.rayTraceBlocks(currPos, nextPos, false, false, false);
            currPos = new Vec3d(posX, posY, posZ);
            nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (rayTraceResult != null) {
                hitPoint = rayTraceResult.hitVec;
                nextPos = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
            }
            Entity entity = findEntityOnPath(currPos, nextPos);
            if (entity != null && !entity.getUniqueID().equals(shooter.getUniqueID())) {
                rayTraceResult = new RayTraceResult(entity);
                hitEntity = true;
            }
            if (rayTraceResult != null && rayTraceResult.typeOfHit != RayTraceResult.Type.MISS) {
                setDead();
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            double motionHorizon = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
            rotationPitch = (float) (MathHelper.atan2(motionY, motionHorizon) * (180D / Math.PI));
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
            float gravity = 0.03F;
            if (item == Items.EXPERIENCE_BOTTLE) {
                gravity = 0.07F;
            } else if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
                gravity = 0.05F;
            }
            if (inWater) {
                friction = 0.8F;
            }
            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;
            motionY -= gravity;
            setPosition(posX, posY, posZ);
        }
        return hitEntity;
    }

    @Override
    public Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity target = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, boundingBox.expand(motionX, motionY, motionZ).grow(1.0));
        double minDistSq = 0.0;
        for (Entity tested : list) {
            if (tested.canBeCollidedWith()) {
                if (shooter == null || ticksExisted >= 2) {
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
        }
        return target;
    }
}
