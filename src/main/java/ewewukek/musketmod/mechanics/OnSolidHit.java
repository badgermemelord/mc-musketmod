package ewewukek.musketmod.mechanics;

import ewewukek.musketmod.BulletEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;
import java.util.Optional;
import java.util.Random;

import static ewewukek.musketmod.mechanics.Penetration.*;

public class OnSolidHit {

    public static double ricochetAngleThreshold = 40.0;
    public static double ricochetVelocityThreshold = 5.0;
    private static final Random random = new Random();

    public static boolean shouldRicochet(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        //double ricochetThreshold = 40.0;
        Random random = new Random();
        double rand = random.nextDouble()+1;

        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        //Vec3 faceNormalVec = getNormalFromFacing(blockHitResult.getDirection());
        Vec3 faceNormalVec = getNormalPhysicsBased(hitResult, projectilePath, level, entity);
        double impactAngle = 90 - getTrajectoryToNormalAngle(projectilePath, faceNormalVec);

        double randomRicochetValue = impactAngle*rand;
        if (randomRicochetValue <= ricochetAngleThreshold && projectilePath.length() > ricochetVelocityThreshold) {
            return true;
        } else {
            return false;
        }
    }

    public static Vec3 evaluateAngleAndReturnRicochetVector(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        double rand = random.nextDouble()+1;
        Vec3 normalVec = getNormalPhysicsBased(hitResult, projectilePath, level, entity);
        double impactAngleRaw = getTrajectoryToNormalAngle(projectilePath, normalVec);
        boolean isNormalInverted = false;

        if(impactAngleRaw > 90) {
            // not inverted
            impactAngleRaw = 180-impactAngleRaw;
        }
        else{
            // inverted
            isNormalInverted = true;
        }
        double impactAngle = 90 - impactAngleRaw;
        System.out.println("impact angle: " + impactAngle);

        if (impactAngle*rand <= ricochetAngleThreshold && projectilePath.length() > ricochetVelocityThreshold) {
            return(getRicochetVectorPhys(projectilePath, normalVec, isNormalInverted, impactAngle));
        } else {
            //Encode extra information
            return new Vec3(255, impactAngle, 0);
        }
    }


    public static Vec3 getRicochetVectorAxisAligned(Vec3 trajectory, HitResult hitResult) {
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Vec3 normal = getNormalFromFacing(blockHitResult.getDirection());
        double impactAngle = 90 - getTrajectoryToNormalAngle(trajectory, normal);
        double remainingVelocity = Math.cos(impactAngle*(Math.PI/180));

        double a0 = normal.x;
        double b0 = normal.y;
        double c0 = normal.z;

        boolean a = a0 != 0.0;
        boolean b = b0 != 0.0;
        boolean c = c0 != 0.0;

        double a1 = trajectory.x;
        double b1 = trajectory.y;
        double c1 = trajectory.z;

        if (a) a1 = -remainingVelocity*a1;
        if (b) b1 = -remainingVelocity*b1;
        if (c) c1 = -remainingVelocity*c1;


        return new Vec3(a1, b1, c1);
    }

    public static Vec3 getRicochetVectorPhys(Vec3 projectilePath, Vec3 oldNormal, boolean isNormalInverted, double impactAngle) {
        double remainingVelocity = Math.cos(impactAngle*(Math.PI/180));
        //Invert normal if necessary
        double inverter = isNormalInverted ? -1 : 1;
        double normalX = oldNormal.x * inverter;
        double normalY = oldNormal.y * inverter;
        double normalZ = oldNormal.z * inverter;
        //Normalise the normal
        double sum = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        double normalisedX = normalX/sum;
        double normalisedY = normalY/sum;
        double normalisedZ = normalZ/sum;

        double angleDot = projectilePath.x * normalisedX + projectilePath.y * normalisedY + projectilePath.z * normalisedZ;
        return new Vec3(
                (projectilePath.x - 2 * normalisedX * angleDot) * remainingVelocity,
                (projectilePath.y - 2 * normalisedY * angleDot) * remainingVelocity,
                (projectilePath.z - 2 * normalisedZ * angleDot) * remainingVelocity
        );
    }

    public static Vec3 getNormalFromFacing(Direction facing) {
        return new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
    }

    public static Vec3 getNormalPhysicsBased(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        Vec3 hitLocation = hitResult.getLocation();
        Vec3 normalisedPath = projectilePath.normalize();
        Vec3 invertedPath = new Vec3(1,1,1).subtract(normalisedPath).scale(0.001);
        Vec3 offsetVector = projectilePath.scale(-0.1);
        Vec3 centreOffset = hitLocation.add(offsetVector);
        Vec3 ray1StartPos = centreOffset.add(invertedPath.x, invertedPath.y, 0);
        Vec3 ray2StartPos = centreOffset.add(0, invertedPath.y, invertedPath.z);
        Vec3 ray3StartPos = centreOffset.add(invertedPath.x, invertedPath.y, invertedPath.z);
        Vec3 ray1Clip = rayCastHitPos(level, ray1StartPos, projectilePath.add(ray1StartPos), entity);
        Vec3 ray2Clip = rayCastHitPos(level, ray2StartPos, projectilePath.add(ray2StartPos), entity);
        Vec3 ray3Clip = rayCastHitPos(level, ray3StartPos, projectilePath.add(ray3StartPos), entity);
        return constructPlaneFromPoints(ray1Clip, ray2Clip, ray3Clip);

    }

    public static Vec3 rayCastHitPos(Level level, Vec3 from, Vec3 to, Entity entity) {
        HitResult hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return hitResult.getLocation();
    }

    public static Vec3 constructPlaneFromPoints(Vec3 A, Vec3 B, Vec3 C) {
        Vec3 AB = new Vec3(B.x - A.x, B.y - A.y, B.z - A.z);
        Vec3 BC = new Vec3(C.x - B.x, C.y - B.y, C.z - B.z);
        return AB.cross(BC);
    }

    public static double getTrajectoryToNormalAngle(Vec3 trajectory, Vec3 normal) {
        boolean usingPhys = true;
        double dotProduct = trajectory.dot(normal);
        double normalMagnitude = normal.length();
        double trajectoryMagnitude = trajectory.length();
        double angleDeg =  Math.acos(dotProduct/(trajectoryMagnitude*normalMagnitude))*57.296;
        if (usingPhys) {
            return angleDeg;
        }
        else {
            return angleDeg-90;
        }
    }


    public static Vec3 AABBRayCastHitPos(Vec3 from, Vec3 to, AABB aabb) {
        Optional<Vec3> hitPos = aabb.clip(from, to);
        return hitPos.orElse(null);
    }

    public static Vec3 evaluateBlockDamage(BlockHitResult hitResult, Level level, BulletEntity bullet) {

        BlockState blockstate = level.getBlockState(hitResult.getBlockPos());
        int hardness;

        return null;
    }

}

