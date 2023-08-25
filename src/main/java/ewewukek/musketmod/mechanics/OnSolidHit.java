package ewewukek.musketmod.mechanics;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;
import java.util.Random;

public class OnSolidHit {

    public static double ricochetAngleThreshold = 40.0;
    public static double ricochetVelocityThreshold = 5.0;

    public static boolean shouldRicochet(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        //double ricochetThreshold = 40.0;
        Random random = new Random();
        double rand = random.nextDouble()+1;

        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        //Vec3 faceNormalVec = getNormalFromFacing(blockHitResult.getDirection());
        Vec3 faceNormalVec = getNormalPhysicsBased(hitResult, projectilePath, level, entity);
        double impactAngle = getTrajectoryToNormalAngle(projectilePath, faceNormalVec);

        double randomRicochetValue = impactAngle*rand;
        if (randomRicochetValue <= ricochetAngleThreshold && projectilePath.length() > ricochetVelocityThreshold) {
            return true;
        } else {
            return false;
        }
    }


    public static Vec3 getRicochetVector(Vec3 trajectory, HitResult hitResult) {
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Vec3 normal = getNormalFromFacing(blockHitResult.getDirection());
        double impactAngle = getTrajectoryToNormalAngle(trajectory, normal);
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

    public static Vec3 getNormalFromFacing(Direction facing) {
        return new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
    }

    public static Vec3 getNormalPhysicsBased(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        Vec3 hitLocation = hitResult.getLocation();
        Vec3 offsetVector = projectilePath.multiply(-0.1, -0.1, -0.1);
        Vec3 centreOffset = hitLocation.add(offsetVector);
        //Approach 1:
        //Vec3 ray1StartPos = centreOffset.
        //Approach 2:
        Vec3 ray1Vector = projectilePath.add(0.001, 0.001, 0);
        Vec3 ray2Vector = projectilePath.add(-0.001, 0.001, 0);
        Vec3 ray3Vector = projectilePath.add(0, -0.001, 0);

        System.out.println("pos0: " + hitLocation);

        Vec3 ray1Clip = rayCastHitPos(level, centreOffset, ray1Vector, entity);
        System.out.println("pos1: " + ray1Clip);
        Vec3 ray2Clip = rayCastHitPos(level, centreOffset, ray2Vector, entity);
        System.out.println("pos2: " + ray2Clip);
        Vec3 ray3Clip = rayCastHitPos(level, centreOffset, ray3Vector, entity);
        System.out.println("pos3: " + ray3Clip);

        Vec3 normal = constructPlaneFromPoints(ray1Clip, ray2Clip, ray3Clip);
        return normal;

    }

    public static Vec3 rayCastHitPos(Level level, Vec3 from, Vec3 to, Entity entity) {
        HitResult hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return hitResult.getLocation();
    }

    public static Vec3 constructPlaneFromPoints(Vec3 A, Vec3 B, Vec3 C) {
        Vec3 AB = new Vec3(B.x - A.x, B.y - A.y, B.z - A.z);
        System.out.println("AB: " + AB);
        Vec3 BC = new Vec3(C.x - B.x, C.y - B.y, C.z - B.z);
        System.out.println("BC: " + BC);
        Vec3 N = AB.cross(BC);
        System.out.println("normal vec: " + N);
        return N;
    }

    public static double getTrajectoryToNormalAngle(Vec3 trajectory, Vec3 normal) {
        boolean usingPhys = true;
        double dotProduct = trajectory.dot(normal);
        double normalMagnitude = normal.length();
        double trajectoryMagnitude = trajectory.length();
        double angle =  Math.acos(dotProduct/(trajectoryMagnitude*normalMagnitude));
        double angleDeg = angle*57.296;

        //System.out.println("l'angle est: " + angleDeg);
        //System.out.println("l'angle final est: " + (angleDeg-90.0));
        if (usingPhys) {
            if(angleDeg > 90) {
                System.out.println("l'angle est: " + (angleDeg-90));
                return angleDeg-90;
            }
            else {
                System.out.println("l'angle est: " + angleDeg);
                return angleDeg;
            }

        }
        else {
            System.out.println("l'angle est: " + (angleDeg-90.0));
            return angleDeg-90;
        }
    }
}
