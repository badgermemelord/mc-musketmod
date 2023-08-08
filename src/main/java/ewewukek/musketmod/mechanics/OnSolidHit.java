package ewewukek.musketmod.mechanics;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;

public class OnSolidHit {

    public static boolean shouldRicochet(HitResult hitResult, Vec3 projectilePath) {
        double ricochetThreshold = 40.0;
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Vec3 faceNormalVec = getNormalFromFacing(blockHitResult.getDirection());
        double impactAngle = getTrajectoryToNormalAngle(projectilePath, faceNormalVec);
        System.out.println("l'amonge: " + impactAngle);
        return impactAngle < ricochetThreshold;
    }


    public static Vec3 getRicochetVector(Vec3 trajectory, HitResult hitResult) {
        BlockHitResult blockHitResult = ((BlockHitResult) hitResult);
        Vec3 normal = getNormalFromFacing(blockHitResult.getDirection());
        System.out.println("le normal: " + normal);

        double a0 = normal.x;
        double b0 = normal.y;
        double c0 = normal.z;

        boolean a = a0 != 0.0;
        boolean b = b0 != 0.0;
        boolean c = c0 != 0.0;
        System.out.println("abc: " + a + " " + b + " " + c);

        double a1 = trajectory.x;
        double b1 = trajectory.y;
        double c1 = trajectory.z;

        if (a) a1 = -a1;
        if (b) b1 = -b1;
        if (c) c1 = -c1;


        return new Vec3(a1, b1, c1);
    }

    public static Vec3 getNormalFromFacing(Direction facing) {
        return new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ());
    }

    public static double getTrajectoryToNormalAngle(Vec3 trajectory, Vec3 normal) {
        double dotProduct = trajectory.dot(normal);
        double normalMagnitude = normal.length();
        double trajectoryMagnitude = trajectory.length();
        double angle =  Math.acos(dotProduct/(trajectoryMagnitude*normalMagnitude));
        double angleDeg = angle*57.296;

        System.out.println("l'angle est: " + angleDeg);
        System.out.println("l'angle final est: " + (angleDeg-90.0));
        return angleDeg-90;
    }

}
