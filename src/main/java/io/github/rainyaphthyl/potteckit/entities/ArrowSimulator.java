package io.github.rainyaphthyl.potteckit.entities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.*;

import javax.annotation.Nonnull;
import java.util.List;

public class ArrowSimulator extends ProjectileSimulator {
    public ArrowSimulator(Entity shooter, WorldClient world) {
        super(shooter, world, 0.5F, 0.5F);
    }

    /**
     * {@link EntityArrow#onUpdate()}
     */
    @Override
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
            RayTraceResult rayTraceResult = world.rayTraceBlocks(currPos, nextPos, false, true, false);
            currPos = new Vec3d(posX, posY, posZ);
            nextPos = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (rayTraceResult != null) {
                hitPoint = rayTraceResult.hitVec;
                nextPos = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
            }
            Entity entity = findEntityOnPath(currPos, nextPos);
            if (entity != null && !entity.getUniqueID().equals(shooter.getUniqueID())) {
                rayTraceResult = new RayTraceResult(entity);
            }
            if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) rayTraceResult.entityHit;
                if (shooter instanceof EntityPlayer && !((EntityPlayer) shooter).canAttackPlayer(entityplayer)) {
                    rayTraceResult = null;
                }
            }
            if (rayTraceResult != null) {
                onHit(rayTraceResult);
                hitEntity = rayTraceResult.entityHit != null;
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

    @Override
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
            float motionSolid = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            posX -= motionX / (double) motionSolid * 0.05000000074505806D;
            posY -= motionY / (double) motionSolid * 0.05000000074505806D;
            posZ -= motionZ / (double) motionSolid * 0.05000000074505806D;
            inGround = true;
        }
        setDead();
    }
}
