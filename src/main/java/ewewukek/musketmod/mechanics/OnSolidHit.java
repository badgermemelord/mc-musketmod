package ewewukek.musketmod.mechanics;

import com.mojang.math.Vector3d;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;
import java.math.MathContext;
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
        double impactAngle = 90 - getTrajectoryToNormalAngle(projectilePath, faceNormalVec);

        double randomRicochetValue = impactAngle*rand;
        if (randomRicochetValue <= ricochetAngleThreshold && projectilePath.length() > ricochetVelocityThreshold) {
            return true;
        } else {
            return false;
        }
    }

    public static Vec3 evaluateAndPerformRicochet(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        Random random = new Random();
        double rand = random.nextDouble()+1;
        Vec3 normalVec = getNormalPhysicsBased(hitResult, projectilePath, level, entity);
        double impactAngleRaw = getTrajectoryToNormalAngle(projectilePath, normalVec);
        boolean isNormalInverted = false;

        if(impactAngleRaw > 90) {
            System.out.println("not inverted");
            isNormalInverted = false;
            impactAngleRaw = 180-impactAngleRaw;
        }
        else{
            System.out.println("inverted");
            isNormalInverted = true;
            impactAngleRaw = impactAngleRaw;
        }

        double impactAngle = 90 - impactAngleRaw;
        System.out.println("impact angle: " + impactAngle);


        double randomRicochetValue = impactAngle*rand;
        if (randomRicochetValue <= ricochetAngleThreshold && projectilePath.length() > ricochetVelocityThreshold) {
            return(getRicochetVectorPhys(projectilePath, normalVec, isNormalInverted, impactAngle, level, entity));
        } else {
            return null;
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

    public static Vec3 getRicochetVectorPhys(Vec3 projectilePath, Vec3 oldNormal, boolean isNormalInverted, double impactAngle, Level level, Entity entity) {
        //double impactAngle = 90 - getTrajectoryToNormalAngle(projectilePath, normal);
        double remainingVelocity = Math.cos(impactAngle*(Math.PI/180));

        double normalX = oldNormal.x;
        double normalY = oldNormal.y;
        double normalZ = oldNormal.z;

        if (isNormalInverted) {
            normalX = normalX*-1;
            normalY = normalY*-1;
            normalZ = normalZ*-1;
        }
        System.out.println("normal inverted vec: " + new Vec3(normalX, normalY, normalZ));

        //Normalise the normal
        //double sum = normalX+normalY+normalZ;
        //double sum = Math.abs(normalX)+Math.abs(normalY)+Math.abs(normalZ);
        double sum = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        double newNormalX = normalX/sum;
        double newNormalY = normalY/sum;
        double newNormalZ = normalZ/sum;

        Vec3 normal = new Vec3(newNormalX, newNormalY, newNormalZ);
        //Vec3 normal = oldNormal.normalize();

        //Vec3 normal = new Vec3(0, 0, 1);
        System.out.println("new normal: " + normal);

        //normal.normalize();

        double inX = projectilePath.x;
        double inY = projectilePath.y;
        double inZ = projectilePath.z;

/*        double normalX = normal.x;
        double normalY = normal.y;
        double normalZ = normal.z;*/

        double dot = projectilePath.dot(normal);

        double outX = (inX - 2*normal.x*dot)*remainingVelocity;
        double outY = (inY - 2*normal.y*dot)*remainingVelocity;
        double outZ = (inZ - 2*normal.z*dot)*remainingVelocity;

        System.out.println("out vec: " + new Vec3(outX, outY, outZ));

        return new Vec3(outX, outY, outZ);

    }

    public static Vec3 getNormalFromFacing(Direction facing) {
        return new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
    }

    public static Vec3 getNormalPhysicsBased(HitResult hitResult, Vec3 projectilePath, Level level, Entity entity) {
        Vec3 hitLocation = hitResult.getLocation();
        Vec3 normalisedPath = projectilePath.normalize();
        System.out.println("normalpath: " +  normalisedPath);
        Vec3 invertedPath = new Vec3(1,1,1).subtract(normalisedPath).scale(0.001);
        System.out.println("invpath: " + invertedPath);

        Vec3 offsetVector = projectilePath.multiply(-0.1, -0.1, -0.1);
        Vec3 centreOffset = hitLocation.add(offsetVector);
        System.out.println("posOffset: " + centreOffset);
        //Approach 1:

        Vec3 ray1StartPos = centreOffset.add(invertedPath.x, invertedPath.y, 0);
        System.out.println("start1: " + ray1StartPos);
        Vec3 ray2StartPos = centreOffset.add(0, invertedPath.y, invertedPath.z);
        System.out.println("start2: " + ray2StartPos);
        Vec3 ray3StartPos = centreOffset.add(invertedPath.x, invertedPath.y, invertedPath.z);
        System.out.println("start3: " + ray3StartPos);

        System.out.println("projectilePath: " + projectilePath);
        System.out.println("offset1: " + ray1StartPos);
        System.out.println("pos0: " + hitLocation);

        Vec3 ray1Clip = rayCastHitPos(level, ray1StartPos, projectilePath.add(ray1StartPos), entity);
        System.out.println("pos1: " + ray1Clip);
        Vec3 ray2Clip = rayCastHitPos(level, ray2StartPos, projectilePath.add(ray2StartPos), entity);
        System.out.println("pos2: " + ray2Clip);
        Vec3 ray3Clip = rayCastHitPos(level, ray3StartPos, projectilePath.add(ray3StartPos), entity);
        System.out.println("pos3: " + ray3Clip);

        /*//Approach 2:
        //Vec3 ray1Vector = new Vec3(-1,0,0);
        Vec3 ray1Vector = projectilePath.add(0.001, 0.001, 0);
        Vec3 ray2Vector = projectilePath.add(-0.001, 0.001, 0);
        Vec3 ray3Vector = projectilePath.add(0, -0.001, 0);

*//*    Vec3 ray1Vector = projectilePath.multiply(1.1, 0.9, 1);
        Vec3 ray2Vector = projectilePath.multiply(0.9, 1, 1.1);
        Vec3 ray3Vector = projectilePath.multiply(1, 1.1, 0.9);*//*

        System.out.println("projectilePath: " + projectilePath);
        System.out.println("vec1: " + ray1Vector);

        System.out.println("pos0: " + hitLocation);

        Vec3 ray1Clip = rayCastHitPos(level, centreOffset, ray1Vector, entity);
        System.out.println("pos1: " + ray1Clip);
        Vec3 ray2Clip = rayCastHitPos(level, centreOffset, ray2Vector, entity);
        System.out.println("pos2: " + ray2Clip);
        Vec3 ray3Clip = rayCastHitPos(level, centreOffset, ray3Vector, entity);
        System.out.println("pos3: " + ray3Clip);*/

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
/*            if(angleDeg > 90) {
                System.out.println("l'angle est-a: " + (180-angleDeg));
                return 180-angleDeg;
            }
            else {
                System.out.println("l'angle est-b: " + angleDeg);
                return angleDeg;
            }*/
            return angleDeg;

        }
        else {
            System.out.println("l'angle est-c: " + (angleDeg-90.0));
            return angleDeg-90;
        }
    }
}
